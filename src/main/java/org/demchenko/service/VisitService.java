package org.demchenko.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.demchenko.dto.*;
import org.demchenko.entity.Doctor;
import org.demchenko.entity.Patients;
import org.demchenko.entity.Visit;
import org.demchenko.exception.BadRequestException;
import org.demchenko.exception.NotFoundException;
import org.demchenko.repository.DoctorRepository;
import org.demchenko.repository.PatientRepository;
import org.demchenko.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class VisitService {
    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public void createVisit(CreateVisitRequest request) {
        Doctor doctor = doctorRepository.findById(request.doctorId()
                )
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        ZoneId doctorZone = ZoneId.of(doctor.getTimezone());
        LocalDateTime start = LocalDateTime.parse(request.start())
                .atZone(doctorZone)
                .toLocalDateTime();
        LocalDateTime end = LocalDateTime.parse(request.end())
                .atZone(doctorZone)
                .toLocalDateTime();

        if (visitRepository.hasOverlappingVisit(request.doctorId(), start, end)) {
            throw new BadRequestException("Doctor already has a visit scheduled for this time");
        }

        Patients patients = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new NotFoundException("Patient not found"));

        Visit visit = new Visit();
        visit.setStartDateTime(start);
        visit.setEndDateTime(end);
        visit.setPatients(patients);
        visit.setDoctor(doctor);

        visitRepository.save(visit);
    }

    public PaginatedResponse getPatients(int page, int size, String search, List<Long> doctorIds) {
        List<Object[]> patientsData = patientRepository.findPatientsWithFilters(
                search, doctorIds, page * size, size);

        if (patientsData.isEmpty()) {
            return new PaginatedResponse(Collections.emptyList(), 0);
        }

        Map<Long, Long> doctorPatientCounts = doctorRepository.countTotalPatientsByDoctorIds(doctorIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0], //doctor id
                        row -> (Long) row[1]  //count of patients
                ));

        List<PatientResponse> patientResponses = new ArrayList<>();
        long totalCount = 0;

        for (Object[] row : patientsData) {
            Patients patients = (Patients) row[0];
            if (totalCount == 0) {
                totalCount = (Long) row[1];
            }

            List<Visit> lastVisits = visitRepository.findLastVisitsForPatientByDoctors(
                    patients.getId(), doctorIds);

            PatientResponse response = new PatientResponse(patients.getFirstName(),
                    patients.getLastName(),
                    lastVisits.stream()
                            .map(visit -> mapToVisitDTO(visit, doctorPatientCounts))
                            .collect(Collectors.toList()));

            patientResponses.add(response);
        }

        return new PaginatedResponse(patientResponses, totalCount);
    }

    private VisitDto mapToVisitDTO(Visit visit, Map<Long, Long> doctorPatientCounts) {
        ZoneId doctorZone = ZoneId.of(visit.getDoctor().getTimezone());

        VisitDto dto = new VisitDto();
        dto.setStart(visit.getStartDateTime()
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(doctorZone)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        dto.setEnd(visit.getEndDateTime()
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(doctorZone)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        DoctorDto doctorDTO = new DoctorDTO(visit.getDoctor().getFirstName(),
                visit.getDoctor().getLastName(),
                doctorPatientCounts.getOrDefault(visit.getDoctor().getId(), 0L));

        dto.setDoctor(doctorDTO);
        return dto;
    }
}
