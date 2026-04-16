package org.example.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateDto {

    private String name;
    private String surname;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String passportDetails;
    private String picName;

}
