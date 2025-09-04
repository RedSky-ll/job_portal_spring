package ir.parsakav.jobportal.auth.service;


import ir.parsakav.jobportal.accounts.domain.Role;
import ir.parsakav.jobportal.accounts.domain.User;
import ir.parsakav.jobportal.accounts.domain.UserProfile;
import ir.parsakav.jobportal.accounts.repo.UserProfileRepository;
import ir.parsakav.jobportal.accounts.repo.UserRepository;
import ir.parsakav.jobportal.auth.dto.LoginRequest;
import ir.parsakav.jobportal.auth.dto.RegisterRequest;
import ir.parsakav.jobportal.auth.dto.ResetPasswordDto;
import ir.parsakav.jobportal.auth.dto.TokenResponse;
import ir.parsakav.jobportal.auth.jwt.JwtService;
import ir.parsakav.jobportal.auth.reset.PasswordResetToken;
import ir.parsakav.jobportal.auth.reset.PasswordResetTokenRepository;
import ir.parsakav.jobportal.core.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Slf4j
@Service @RequiredArgsConstructor
public class AuthService {
    private final PasswordResetTokenRepository resetTokens;
    private final MailService mailService;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;
private final UserProfileRepository profileRepository;
    @Transactional
    public TokenResponse register(RegisterRequest req) {
        if (users.existsByEmail(req.getEmail())) throw new IllegalArgumentException("Email exists");
        Role role = Role.valueOf(req.getRole().toUpperCase());
        User u = User.builder()
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .role(role)
                .enabled(false)         // در نسخهٔ مینیمال: فعال
                .emailVerified(false)   // در نسخهٔ مینیمال: تأیید شده
                .build();
        users.save(u);
        if(req.getRole().equals("SEEKER")) {

            profileRepository.save(UserProfile.builder()
                    .user(u)
                    .fullName(req.getFullName())
                    .build());
        }
        // TODO: send verify email (اختیاری)
        String token = jwt.generateToken(u.getEmail(), u.getRole().name());
      sendVerifyEmail(u,token);
        return new TokenResponse(token);
    }

    public TokenResponse login(LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User u = users.findByEmail(req.getEmail()).orElseThrow();
        return new TokenResponse(jwt.generateToken(u.getEmail(), u.getRole().name()));
    }

    public void sendVerifyEmail(User u, String token) {
        String url = "https://localhost:8443/auth/verify-email?token="+token;
        String html = """
    <h3>Verify your email</h3>
    <p>Click the link below to activate your account:</p>
    <p><a href="%s">%s</a></p>
  """.formatted(url, url);
        try { mailService.sendHtml(u.getEmail(), "Verify your email", html); }
        catch ( jakarta.mail.MessagingException e) { throw new RuntimeException(e); }
    }

    @Value("${app.base-url:https://localhost:8443}")
    private String appBaseUrl;

    @Value("${app.reset.ttl-minutes:60}")
    private long resetTtlMinutes;

    private String genResetToken() {
        byte[] b = new byte[24]; // 192-bit
        new SecureRandom().nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    /** درخواست ریست: ایمیل حاوی لینک می‌فرستد (اگر کاربر وجود داشته باشد). */
    @Transactional
    public void sendReset(String email) {
        var opt = users.findByEmail(email);
        // برای عدم افشای وجود/عدم وجود ایمیل، اگر نبود هم ساکت OK برگرد
        if (opt.isEmpty()) {
            log.info("Password reset requested for non-existent email {}", email);
            return;
        }
        User u = opt.get();

        // توکن‌های قدیمی کاربر را پاک کن (اختیاری/ایمن‌تر)
        resetTokens.deleteByUserId(u.getId());

        var token = PasswordResetToken.builder()
                .user(u)
                .token(genResetToken())
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plus(Duration.ofMinutes(resetTtlMinutes)))
                .build();
        resetTokens.save(token);

        String link = appBaseUrl + "/auth/reset-password?token=" + token.getToken();
        String html = """
      <h3>Reset your password</h3>
      <p>Click the link below to set a new password (valid %d minutes):</p>
      <p><a href="%s" target="_blank">%s</a></p>
      <p>If you did not request this, you can ignore this email.</p>
    """.formatted(resetTtlMinutes, link, link);

        try {
            mailService.sendHtml(u.getEmail(), "Reset your password", html);
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /** انجام ریست: توکن را چک می‌کند و پسورد جدید را ست می‌کند. */
    @Transactional
    public void reset(ResetPasswordDto dto) {
        var opt = resetTokens.findByToken(dto.getToken());
        if (opt.isEmpty()) throw new IllegalArgumentException("Invalid token");

        var t = opt.get();
        if (t.getUsedAt() != null) throw new IllegalArgumentException("Token already used");
        if (t.getExpiresAt().isBefore(Instant.now())) throw new IllegalArgumentException("Token expired");

        var newPass = dto.getNewPassword();
        if (newPass == null || newPass.length() < 8)
            throw new IllegalArgumentException("Password too short (min 8)");

        var u = t.getUser();
        u.setPassword(encoder.encode(newPass));
        users.save(u);

        // مصرف توکن
        t.setUsedAt(Instant.now());
        resetTokens.save(t);

        // (اختیاری) سایر توکن‌های کاربر را هم باطل کن
        resetTokens.deleteByUserId(u.getId());
    }
    public void verifyEmail(String token) {
        // ایمیل کاربر رو از روی توکن دربیار
        String email = jwt.extractUsername(token);

        User user = users.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        // تغییر وضعیت
        user.setEnabled(true);
        user.setEmailVerified(true);

        System.out.println("Sanazz");
        users.save(user);

    }
}
