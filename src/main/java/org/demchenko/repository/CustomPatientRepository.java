package org.demchenko.repository;

import java.util.List;

public interface CustomPatientRepository {
    List<Object[]> findPatientsWithFilters(List<String> searchTerms, List<Long> doctorIds, int offset, int size);
}
