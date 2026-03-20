package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.enums.Gender;
import org.example.model.enums.Role;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String surname;
    private String email;
    private String phone;
    private String password;
    private String passportDetails;
    private String picName;
    private boolean isBlocked = false;
    private LocalDate createdAt =  LocalDate.now();
    @Enumerated(EnumType.STRING)
    private Role role;
}

