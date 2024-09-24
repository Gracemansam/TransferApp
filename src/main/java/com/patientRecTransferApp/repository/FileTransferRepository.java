package com.patientRecTransferApp.repository;

import com.patientRecTransferApp.entity.FileTransfer;
import com.patientRecTransferApp.entity.Hospital;
import com.patientRecTransferApp.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileTransferRepository extends JpaRepository<FileTransfer, Long> {
    List<FileTransfer> findByPatient(Patient patient);
    List<FileTransfer> findBySourceHospital(Hospital hospital);
    List<FileTransfer> findByDestinationHospital(Hospital hospital);
}