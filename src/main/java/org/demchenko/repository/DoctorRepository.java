package org.demchenko.repository;

import org.demchenko.dto.DoctorPatientCounts;
import org.demchenko.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query("""
        SELECT new org.demchenko.dto.DoctorPatientCounts(d.id, COUNT(DISTINCT v.patient.id))
        FROM Doctor d
        LEFT JOIN Visit v ON v.doctor.id = d.id
        WHERE d.id IN :doctorIds
        GROUP BY d.id
        """)
    List<DoctorPatientCounts> countTotalPatientsByDoctorIds(List<Long> doctorIds);

    @Query("""
        SELECT new org.demchenko.dto.DoctorPatientCounts(d.id, COUNT(DISTINCT v.patient.id))
        FROM Doctor d
        LEFT JOIN Visit v ON v.doctor.id = d.id
        GROUP BY d.id
        """)
    List<DoctorPatientCounts> countTotalPatientsForAllDoctors();
}
