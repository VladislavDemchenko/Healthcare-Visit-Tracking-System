package org.demchenko.repository;

import org.demchenko.entity.Patients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patients, Long> {
    @Query(value = """
        SELECT p.*, COUNT(*) OVER() as total_count
        FROM patients p
        WHERE (:search IS NULL OR
               LOWER(CONCAT(p.first_name, ' ', p.last_name)) LIKE LOWER(CONCAT('%', :search, '%')))
        AND EXISTS (
            SELECT 1 FROM visits v
            WHERE v.patient_id = p.id
            AND (:doctorIds IS NULL OR v.doctor_id IN (:doctorIds))
        )
        ORDER BY p.id
        LIMIT :size OFFSET :offset
        """, nativeQuery = true)
    List<Object[]> findPatientsWithFilters(String search, List<Long> doctorIds, int offset, int size);
}
