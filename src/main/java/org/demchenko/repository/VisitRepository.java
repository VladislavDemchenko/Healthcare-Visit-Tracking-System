package org.demchenko.repository;

import org.demchenko.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    @Query("""
        SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END
        FROM Visit v
        WHERE v.doctor.id = :doctorId
        AND ((v.startDateTime <= :end AND v.endDateTime >= :start))
        """)
    boolean hasOverlappingVisit(Long doctorId, LocalDateTime start, LocalDateTime end);

    @Query("""
        SELECT v FROM Visit v
        WHERE v.patient.id IN :patientIds
        AND v.doctor.id IN :doctorIds
        """)
    List<Visit> findVisitsForPatientsAndDoctors(List<Long> patientIds, List<Long> doctorIds);

    @Query("""
        SELECT v FROM Visit v
        WHERE v.patient.id IN :patientIds
        """)
    List<Visit> findVisitsForPatients(List<Long> patientIds);
}
