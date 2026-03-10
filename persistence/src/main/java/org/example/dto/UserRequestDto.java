package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.Role;

import java.time.LocalDate;

@Getter
@Setter
public class UserRequestDto {

    private int id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String passportDetails;
    private String picName;
    private Role role;
    private LocalDate createdAt;
}
