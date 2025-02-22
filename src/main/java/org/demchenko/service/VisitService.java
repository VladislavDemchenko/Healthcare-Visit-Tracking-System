package org.demchenko.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.demchenko.dto.*;
import org.demchenko.entity.Doctor;
import org.demchenko.entity.Patient;
import org.demchenko.entity.Visit;
import org.demchenko.exception.BadRequestException;
import org.demchenko.exception.NotFoundException;
import org.demchenko.repository.CustomPatientRepository;
import org.demchenko.repository.DoctorRepository;
import org.demchenko.repository.PatientRepository;
import org.demchenko.repository.VisitRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class VisitService {
    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final CustomPatientRepository customPatientRepository;

    // but if we will have some changes in db without service, we will have old data in cache
    @CacheEvict(value = "patients", allEntries = true)
    public void createVisit(CreateVisitRequest request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new NotFoundException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        ZoneId doctorZone = ZoneId.of(doctor.getTimezone());
        LocalDateTime start = LocalDateTime.parse(request.getStart())
                .atZone(doctorZone)
                .toLocalDateTime();
        LocalDateTime end = LocalDateTime.parse(request.getEnd())
                .atZone(doctorZone)
                .toLocalDateTime();

        if (visitRepository.hasOverlappingVisit(request.getDoctorId(), start, end)) {
            throw new BadRequestException("Doctor already has a visit scheduled for this time");
        }

        Visit visit = new Visit();
        visit.setStartDateTime(start);
        visit.setEndDateTime(end);
        visit.setPatient(patient);
        visit.setDoctor(doctor);

        visitRepository.save(visit);
    }
    @Cacheable("patients")
    public PaginatedResponse getPatients(int page, int size, List<String> search, List<Long> doctorIds) {
        List<PatientDto> patientsData = customPatientRepository.findPatientsWithFilters(search, doctorIds, page * size, size);

        if (patientsData.isEmpty()) {
            return new PaginatedResponse(Collections.emptyList(), 0);
        }

        // All patients ids
        List<Long> patientIds = patientsData.stream()
                .map(PatientDto::getId)
                .collect(Collectors.toList());

        // Visits of patients by doctor
        List<Visit> visits = (doctorIds == null || doctorIds.isEmpty())
                ? visitRepository.findVisitsForPatients(patientIds)
                : visitRepository.findVisitsForPatientsAndDoctors(patientIds, doctorIds);

        // Visits for all patients. Where kay is patient id and value is list of visits
        Map<Long, List<Visit>> patientVisitsMap = visits.stream()
                .collect(Collectors.groupingBy(visit -> visit.getPatient().getId()));

        // Count of patients for each doctor. Where key is doctor id and value is count of patients
        Map<Long, Long> doctorPatientCounts = (doctorIds == null || doctorIds.isEmpty())
                ? doctorRepository.countTotalPatientsForAllDoctors()
                    .stream()
                    .collect(Collectors.toMap(DoctorPatientCounts::getDoctorId, DoctorPatientCounts::getPatientCount))
                : doctorRepository.countTotalPatientsByDoctorIds(doctorIds)
                    .stream()
                    .collect(Collectors.toMap(DoctorPatientCounts::getDoctorId, DoctorPatientCounts::getPatientCount));

        // Map patient data to response with visits
        List<PatientResponse> patientResponses = patientsData.stream()
                .map(patient -> new PatientResponse(
                        patient.getFirstName(),
                        patient.getLastName(),

                        patientVisitsMap.getOrDefault(patient.getId(), Collections.emptyList())
                                .stream()
                                .map(visit -> mapToVisitDTO(visit, doctorPatientCounts))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new PaginatedResponse(patientResponses, patientsData.size());
    }

    private VisitDto mapToVisitDTO(Visit visit, Map<Long, Long> doctorPatientCounts) {
        ZoneId doctorZone = ZoneId.of(visit.getDoctor().getTimezone());

        DoctorDto doctorDTO = new DoctorDto(visit.getDoctor().getFirstName(),
                visit.getDoctor().getLastName(),
                doctorPatientCounts.getOrDefault(visit.getDoctor().getId(), 0L));

        return new VisitDto(
                visit.getStartDateTime()
                        .atZone(ZoneId.systemDefault())
                        .withZoneSameInstant(doctorZone)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                visit.getEndDateTime()
                        .atZone(ZoneId.systemDefault())
                        .withZoneSameInstant(doctorZone)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                doctorDTO
        );
    }
}
