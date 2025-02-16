package org.demchenko.repository;

import org.demchenko.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query("""
        SELECT d.id, COUNT(DISTINCT v.patients.id)
        FROM Doctor d
        LEFT JOIN Visit v ON v.doctor.id = d.id
        WHERE d.id IN :doctorIds
        GROUP BY d.id
        """)
    List<Object[]> countTotalPatientsByDoctorIds(List<Long> doctorIds);
}
