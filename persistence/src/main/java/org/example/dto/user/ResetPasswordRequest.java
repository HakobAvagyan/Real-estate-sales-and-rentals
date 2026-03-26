package org.example.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "New password must not be blank")
    @Size(min = 8, max = 20, message = "New password must be between 8 and 20 characters")
    private String newPassword;

    @NotBlank(message = "Confirm password must not be blank")
    private String confirmPassword;

}
