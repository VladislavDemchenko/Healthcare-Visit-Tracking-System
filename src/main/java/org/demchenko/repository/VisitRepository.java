package org.demchenko.repository;

import org.demchenko.entity.Visit;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "lastVisitsCache", key = "#patientId")
    @Query("""
        SELECT v FROM Visit v
        WHERE v.patients.id = :patientId
        AND v.doctor.id IN :doctorIds
        AND v.id IN (
            SELECT MAX(v2.id)
            FROM Visit v2
            WHERE v2.patients.id = :patientId
            AND v2.doctor.id IN :doctorIds
            GROUP BY v2.doctor.id
        )
        """)
    List<Visit> findLastVisitsForPatientByDoctors(Long patientId, List<Long> doctorIds);
}
