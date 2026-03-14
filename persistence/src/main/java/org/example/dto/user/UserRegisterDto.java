package org.example.dto.user;

import lombok.Data;
import org.example.model.enums.Gender;
import org.example.model.enums.Role;

import java.time.LocalDate;

@Data
public class UserRegisterDto {

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
    private LocalDate birthDate;
    private Gender gender;
    private Role role;

}
