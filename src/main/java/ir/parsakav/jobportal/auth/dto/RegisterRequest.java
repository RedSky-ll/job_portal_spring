package ir.parsakav.jobportal.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email @NotBlank private String email;
    @Size(min = 6) @NotBlank private String password;
    @NotBlank private String role; // SEEKER/EMPLOYER
    private String fullName;
}