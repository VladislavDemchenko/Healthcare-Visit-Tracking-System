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
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class VisitService {
    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public void createVisit(CreateVisitRequest request) {

        Patients patients = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new NotFoundException("Patient not found"));

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

        Visit visit = new Visit();
        visit.setStartDateTime(start);
        visit.setEndDateTime(end);
        visit.setPatients(patients);
        visit.setDoctor(doctor);

        visitRepository.save(visit);
    }

    public PaginatedResponse getPatients(int page, int size, String search, String doctorIds) {

        List<Long> doctorIdList = parseCommaSeparatedLongs(doctorIds);

        List<Object[]> patientsData = patientRepository.findPatientsWithFilters(
                search, doctorIds, page * size, size);

        if (patientsData.isEmpty()) {
            return new PaginatedResponse(Collections.emptyList(), 0);
        }

        Map<Long, Long> doctorPatientCounts = doctorRepository.countTotalPatientsByDoctorIds(doctorIdList)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0], //doctor id
                        row -> (Long) row[1]  //count of patients
                ));

        List<PatientResponse> patientResponses = new ArrayList<>();
        long totalCount = 0;

        for (Object[] row : patientsData) {
            Long patientId = ((Number) row[0]).longValue();
            String firstName = (String) row[1];
            String lastName = (String) row[2];
            if (totalCount == 0) {
                totalCount = ((Number) row[3]).longValue();
            }

            List<Visit> lastVisits = visitRepository.findLastVisitsForPatientByDoctors(
                    patientId, doctorIdList);

            PatientResponse response = new PatientResponse(
                    firstName,
                    lastName,
                    lastVisits.stream()
                            .map(visit -> mapToVisitDTO(visit, doctorPatientCounts))
                            .collect(Collectors.toList())
            );

            patientResponses.add(response);
        }

        return new PaginatedResponse(patientResponses, totalCount);
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

    public static List<Long> parseCommaSeparatedLongs(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        try {
            return Arrays.stream(input.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid number format in list: " + input);
        }
    }
}
