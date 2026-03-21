package org.example.dto.user;

import lombok.Data;

@Data
public class UserChangePasswordDto {
    private String email;
    private String password;
}
