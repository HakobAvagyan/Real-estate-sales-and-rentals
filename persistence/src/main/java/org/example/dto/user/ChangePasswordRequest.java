package org.example.dto.user;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String newPassword;
    private String oldPassword;
    private String confirmPassword;

}
