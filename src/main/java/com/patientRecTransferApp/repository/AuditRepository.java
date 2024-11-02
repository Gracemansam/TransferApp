package com.patientRecTransferApp.repository;

import com.patientRecTransferApp.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, Long> {
    @Query("SELECT a FROM AuditLog a WHERE a.recipientFacilityId = :facilityId OR a.senderFacilityId = :facilityId")
    List<AuditLog> findByFacilityId(@Param("facilityId") Long facilityId);
}
