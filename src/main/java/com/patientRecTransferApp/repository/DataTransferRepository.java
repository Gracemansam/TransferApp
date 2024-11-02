package com.patientRecTransferApp.repository;

import com.patientRecTransferApp.entity.DataTransferRequest;
import com.patientRecTransferApp.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataTransferRepository extends JpaRepository<DataTransferRequest, Long> {

    List<DataTransferRequest> findByStatus(String status);

    List<DataTransferRequest> findByRecipientFacilityAndStatus(Long recipientFacilityId,String status);

    @Query("SELECT COUNT(d) FROM DataTransferRequest d WHERE d.appUserId = :appUserId AND d.status = :status")
    long countByStatusAndAppUserId(@Param("status") String status, @Param("appUserId") Long appUserId);

    @Query("SELECT d FROM DataTransferRequest d WHERE d.status = 'PENDING' AND d.recipientFacility = :recipientFacilityId")
    List<DataTransferRequest> findPendingByRecipientFacilityId(@Param("recipientFacilityId") Long recipientFacilityId);

    @Query("SELECT d FROM DataTransferRequest d WHERE d.status = 'COMPLETED' AND d.requestingFacility = :requestingFacilityId")
    List<DataTransferRequest> findCompletedByRequestingFacilityId(@Param("requestingFacilityId") Long requestingFacilityId);


    DataTransferRequest findTopByRequestingFacilityAndRecipientFacilityAndStatusOrderByRequestTimeDesc(
            Long requestingFacility, Long recipientFacility, String status);
}