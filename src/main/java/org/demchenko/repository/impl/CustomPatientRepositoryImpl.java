package org.demchenko.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.demchenko.dto.PatientDto;
import org.demchenko.dto.QueryParameter;
import org.demchenko.repository.CustomPatientRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomPatientRepositoryImpl implements CustomPatientRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PatientDto> findPatientsWithFilters(List<String> searchTerms, List<Long> doctorIds, int offset, int size) {
        StringBuilder sql = new StringBuilder("""
        SELECT p.*
        FROM patients p
        WHERE 1=1
    """);

        List<QueryParameter<?>> params = new ArrayList<>();

        if (searchTerms != null && !searchTerms.isEmpty()) {
            sql.append(" AND (");
            for (int i = 0; i < searchTerms.size(); i++) {
                String paramName = "searchTerm" + i;
                sql.append(" LOWER(CONCAT(p.first_name, ' ', p.last_name)) LIKE LOWER(CONCAT('%', :").append(paramName).append(", '%')) ");
                params.add(new QueryParameter<>(paramName, searchTerms.get(i)));

                if (i < searchTerms.size() - 1) {
                    sql.append(" OR ");
                }
            }
            sql.append(")");
        }

        if (doctorIds != null && !doctorIds.isEmpty()) {
            sql.append("""
            AND EXISTS (
                SELECT 1 FROM visits v
                WHERE v.patient_id = p.id
                AND v.doctor_id IN (:doctorIds)
            )
        """);
            params.add(new QueryParameter<>("doctorIds", doctorIds));
        }

        sql.append(" ORDER BY p.id LIMIT :size OFFSET :offset");
        params.add(new QueryParameter<>("size", size));
        params.add(new QueryParameter<>("offset", offset));

        Query query = entityManager.createNativeQuery(sql.toString(), PatientDto.class);
        params.forEach(param -> param.applyTo(query));

        return query.getResultList();
    }
}
