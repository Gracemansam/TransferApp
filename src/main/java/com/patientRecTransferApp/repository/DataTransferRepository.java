package com.patientRecTransferApp.repository;

import com.patientRecTransferApp.entity.DataTransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataTransferRepository extends JpaRepository<DataTransferRequest, Long> {
}