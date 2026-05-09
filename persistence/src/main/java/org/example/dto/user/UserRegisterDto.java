package org.example.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.model.enums.Gender;
import org.example.model.enums.Role;

import java.time.LocalDate;

@Data
public class UserRegisterDto {

    private int id;

    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Surname must not be blank")
    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
    private String surname;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone must be a valid number")
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
