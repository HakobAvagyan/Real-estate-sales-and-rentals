package org.example.dto.user;

import lombok.Data;
import org.example.model.enums.Gender;
import org.example.model.enums.Role;

import java.time.LocalDate;

@Data
public class UserResponseDto {

    private int id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String passportDetails;
    private String picName;
    private Role role;
    private LocalDate birthDate;
    private Gender gender;
    private boolean isBlocked;
    private LocalDate createdAt;
}
