package com.patientRecTransferApp.repository;

import com.patientRecTransferApp.entity.ConsentRequest;
import com.patientRecTransferApp.entity.Hospital;
import com.patientRecTransferApp.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsentRequestRepository extends JpaRepository<ConsentRequest, Long> {
    List<ConsentRequest> findByPatientAndStatus(Patient patient, String status);
    List<ConsentRequest> findByRequestingHospitalAndStatus(Hospital hospital, String status);
}