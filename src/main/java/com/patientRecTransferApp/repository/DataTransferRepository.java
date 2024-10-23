package com.patientRecTransferApp.repository;

import com.patientRecTransferApp.entity.DataTransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataTransferRepository extends JpaRepository<DataTransferRequest, Long> {

    List<DataTransferRequest> findByStatus(String status);

    DataTransferRequest findTopByRequestingFacilityAndRecipientFacilityAndStatusOrderByRequestTimeDesc(
            Long requestingFacility, Long recipientFacility, String status);
}