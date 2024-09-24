package com.patientRecTransferApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_users")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppUser  extends  BaseEntity{


    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;


    @Column(name = "gender")
    private String gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "app_users_permissions", joinColumns = @JoinColumn(name = "app_users_id"))
    @Column(name = "permission")
    private Set<String> permissions = new HashSet<>();

    @Column(nullable = false)
    private boolean active = true;



    @Version
    private Long version;






    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    public void removePermission(String permission) {
        this.permissions.remove(permission);
    }

    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }


}