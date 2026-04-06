package org.example.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.enums.Role;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateDto {

    private int id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String passportDetails;
    @NotBlank(message = "New password must not be blank")
    @Size(min = 8, max = 20, message = "New password must be between 8 and 20 characters")
    private String password;
    private String picName;
    private Role role;

}
