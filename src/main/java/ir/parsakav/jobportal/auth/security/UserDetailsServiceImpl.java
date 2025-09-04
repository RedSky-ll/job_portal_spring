package ir.parsakav.jobportal.auth.security;

import ir.parsakav.jobportal.accounts.domain.User;
import ir.parsakav.jobportal.accounts.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository users;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = users.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Not found"));
        return new UserPrincipal(u);
    }
}
