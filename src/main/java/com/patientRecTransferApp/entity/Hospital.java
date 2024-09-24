package com.patientRecTransferApp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "hospitals")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hospital  extends BaseEntity{


    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "email")
    private String email;

    // Getters and setters
}