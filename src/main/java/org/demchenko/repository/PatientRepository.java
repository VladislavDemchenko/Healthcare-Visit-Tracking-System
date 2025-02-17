package org.demchenko.repository;

import org.demchenko.entity.Patients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patients, Long> {
    @Query(value = """
        SELECT p.id, p.first_name, p.last_name, COUNT(*) OVER() as total_count
        FROM patients p
        LEFT JOIN visits v ON v.patient_id = p.id
        WHERE (:search IS NULL OR LOWER(CONCAT(p.first_name, ' ', p.last_name)) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:doctorIds IS NULL OR FIND_IN_SET(v.doctor_id, :doctorIds) > 0)
        ORDER BY p.id
        LIMIT :size OFFSET :offset
        """, nativeQuery = true)
    List<Object[]> findPatientsWithFilters(
            @Param("search") String search,
            @Param("doctorIds") String doctorIds,
            @Param("offset") int offset,
            @Param("size") int size
    );

}
