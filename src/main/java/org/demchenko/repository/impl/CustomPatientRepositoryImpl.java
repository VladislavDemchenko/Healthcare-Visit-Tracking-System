package org.demchenko.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.demchenko.repository.CustomPatientRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CustomPatientRepositoryImpl implements CustomPatientRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> findPatientsWithFilters(List<String> searchTerms, List<Long> doctorIds, int offset, int size) {
        StringBuilder sql = new StringBuilder("""
            SELECT p.*, COUNT(*) OVER() as total_count
            FROM patients p
            WHERE 1=1
        """);

        Map<String, Object> params = new HashMap<>();

        if (searchTerms != null && !searchTerms.isEmpty()) {
            sql.append(" AND (");
            for (int i = 0; i < searchTerms.size(); i++) {
                String paramName = "searchTerm" + i;
                sql.append(" LOWER(CONCAT(p.first_name, ' ', p.last_name)) LIKE LOWER(CONCAT('%', :" + paramName + ", '%')) ");
                params.put(paramName, searchTerms.get(i));

                if (i < searchTerms.size() - 1) {
                    sql.append(" OR ");
                }
            }
            sql.append(")");
        }

        if (doctorIds != null && !doctorIds.isEmpty()) {
            sql.append(" AND EXISTS ( SELECT 1 FROM visits v WHERE v.patient_id = p.id AND v.doctor_id IN (:doctorIds) )");
            params.put("doctorIds", doctorIds);
        }

        sql.append(" ORDER BY p.id LIMIT :size OFFSET :offset");
        params.put("size", size);
        params.put("offset", offset);

        Query query = entityManager.createNativeQuery(sql.toString());
        params.forEach(query::setParameter);

        return query.getResultList();
    }
}
