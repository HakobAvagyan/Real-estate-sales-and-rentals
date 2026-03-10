package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.Role;

import java.time.LocalDate;

@Getter
@Setter
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
    private Role role;

}
