package ir.parsakav.jobportal.auth.api;

import ir.parsakav.jobportal.accounts.service.ProfileService;
import ir.parsakav.jobportal.auth.dto.LoginRequest;
import ir.parsakav.jobportal.auth.dto.RegisterRequest;
import ir.parsakav.jobportal.auth.dto.ResetPasswordDto;
import ir.parsakav.jobportal.auth.dto.TokenResponse;
import ir.parsakav.jobportal.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// auth/api/AuthController.java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
private final ProfileService profileService;
    @PostMapping("/register")
    public TokenResponse register(@Valid @RequestBody RegisterRequest req) {

        return authService.register(req); // ارسال ایمیل verify با token جداگانه
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
    // ir/parsakav/jobportal/auth/api/AuthController.java (اضافه)
    @PostMapping("/logout")
    public Map<String, String> logout() {
        return Map.of("message", "Logged out");
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return "Email verified.";
    }

    @PostMapping("/forgot-password")
    public void forgot(@RequestParam String email) { authService.sendReset(email); }

    @PostMapping("/reset-password")
    public void reset(@RequestBody ResetPasswordDto dto) { authService.reset(dto); }
}
