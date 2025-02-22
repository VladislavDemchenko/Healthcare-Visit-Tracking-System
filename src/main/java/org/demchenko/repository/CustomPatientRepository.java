package org.demchenko.repository;

import org.demchenko.dto.PatientDto;

import java.util.List;

public interface CustomPatientRepository {
    List<PatientDto> findPatientsWithFilters(List<String> searchTerms, List<Long> doctorIds, int offset, int size);
}
