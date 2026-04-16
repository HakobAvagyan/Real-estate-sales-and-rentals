package org.example.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "New password must not be blank")
    @Size(min = 8, max = 20, message = "New password must be between 8 and 20 characters")
    private String password;

    private String passportDetails;
    private String picName;
    private boolean isBlocked;
    private LocalDate createdAt =  LocalDate.now();
    private LocalDate birthDate;
    private String verificationCode;
    private Gender gender;
    private Role role;

}
