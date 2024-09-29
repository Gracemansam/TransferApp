package com.patientRecTransferApp.serviceImpl;

import com.patientRecTransferApp.entity.ConsentRequest;
import com.patientRecTransferApp.entity.FileTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendConsentRequestNotification(String toEmail, ConsentRequest consentRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("New Consent Request for Medical Records Transfer");
        message.setText("Dear Patient,\n\n" +
                "A consent request has been initiated for the transfer of your medical records.\n" +
                "Requesting Hospital: " + consentRequest.getRequestingHospital().getName() + "\n" +
                "Holding Hospital: " + consentRequest.getHoldingHospital().getName() + "\n" +
                "Request Date: " + consentRequest.getRequestDate() + "\n\n" +
                "Please log in to your patient portal to approve or deny this request.\n\n" +
                "Thank you,\nPatient Record Transfer System");
        emailSender.send(message);
    }

    public void sendConsentDecisionNotification(String toEmail, ConsentRequest consentRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Update on Consent Request for Medical Records Transfer");
        message.setText("Dear Hospital Administrator,\n\n" +
                "A decision has been made regarding the consent request for patient " + consentRequest.getPatient().getFirstName() + ".\n" +
                "Request Status: " + consentRequest.getStatus() + "\n" +
                "Decision Date: " + consentRequest.getResponseDate() + "\n\n" +
                (consentRequest.getStatus().equals("APPROVED")
                        ? "The file transfer process will be initiated shortly."
                        : "No further action is required at this time.") + "\n\n" +
                "Thank you,\nPatient Record Transfer System");
        emailSender.send(message);
    }

    public void sendFileUploadRequest(String toEmail, FileTransfer fileTransfer) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Request to Upload Patient Medical Records");
        message.setText("Dear Hospital Administrator,\n\n" +
                "A file transfer has been initiated for patient " + fileTransfer.getPatient().getFirstName() + " " + fileTransfer.getPatient().getLastName() + ".\n" +
                "Source Hospital: " + fileTransfer.getSourceHospital().getName() +
                "Destination Hospital: " + fileTransfer.getDestinationHospital().getName() + "\n" +
                "Transfer ID: " + fileTransfer.getId() + "\n\n" +
                "Please upload the encrypted patient records through the secure file transfer system.\n\n" +
                "Thank you,\nPatient Record Transfer System");
        emailSender.send(message);
    }

    public void sendDecryptionKeyToPatient(String toEmail, String encryptedDecryptionKey) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Decryption Key for Your Medical Records");
        message.setText("Dear Patient,\n\n" +
                "Your medical records have been encrypted for secure transfer. Below is your encrypted decryption key:\n\n" +
                encryptedDecryptionKey + "\n\n" +
                "Please keep this key safe and secure. You will need it to decrypt your medical records when accessing them at the new hospital.\n\n" +
                "To decrypt this key, you will need to use your secret answer that you provided during registration.\n\n" +
                "Thank you,\nPatient Record Transfer System");
        emailSender.send(message);
    }

    public void sendFileTransferCompletionNotification(String toEmail, FileTransfer fileTransfer) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Medical Records Transfer Completed");
        message.setText("Dear Hospital Administrator,\n\n" +
                "The file transfer for patient " + fileTransfer.getPatient().getFirstName() + " " + fileTransfer.getPatient().getLastName()+ " has been completed.\n" +
                "Source Hospital: " + fileTransfer.getSourceHospital().getName() + "\n" +
                "Destination Hospital: " + fileTransfer.getDestinationHospital().getName() + "\n" +
                "Transfer ID: " + fileTransfer.getId() + "\n" +
                "Transfer Date: " + fileTransfer.getTransferDate() + "\n\n" +
                "The encrypted files are now available in your secure system.\n\n" +
                "Thank you,\nPatient Record Transfer System");
        emailSender.send(message);
    }
}