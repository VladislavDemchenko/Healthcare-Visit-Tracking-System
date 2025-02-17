package org.demchenko.repository;

import org.demchenko.entity.Patients;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patients, Long> {
}
