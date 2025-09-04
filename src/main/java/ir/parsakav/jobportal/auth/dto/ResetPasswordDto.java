package ir.parsakav.jobportal.auth.dto;


import lombok.Data;

@Data
public class ResetPasswordDto {
    private String token;
    private String newPassword;
}
