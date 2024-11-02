package com.patientRecTransferApp.repository;

import com.patientRecTransferApp.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Optional<Hospital> findByNameIgnoreCase(String name);
    Optional<Hospital>findById(Long id);

}