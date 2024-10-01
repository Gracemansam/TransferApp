package com.patientRecTransferApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_logs")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferLog extends BaseEntity {


    @ManyToOne
    @JoinColumn(name = "file_transfer_id", nullable = false)
    private FileTransfer fileTransfer;

    @Column(name = "log_date")
    private LocalDateTime logDate;

    @Column(name = "log_message")
    private String logMessage;

    @Column(name = "log_level")
    private String logLevel;

}
