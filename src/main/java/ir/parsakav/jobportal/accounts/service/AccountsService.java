package ir.parsakav.jobportal.accounts.service;


import ir.parsakav.jobportal.accounts.domain.Role;
import ir.parsakav.jobportal.accounts.domain.User;
import ir.parsakav.jobportal.accounts.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service @RequiredArgsConstructor
public class AccountsService {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Transactional
    public User ensureUserFromGoogle(String email) {
        return users.findByEmail(email).orElseGet(() -> {
            User u = User.builder()
                    .email(email)
                    .password(encoder.encode(UUID.randomUUID().toString()))
                    .role(Role.SEEKER)
                    .enabled(true)
                    .emailVerified(true)
                    .build();
            return users.save(u);
        });
    }
}
