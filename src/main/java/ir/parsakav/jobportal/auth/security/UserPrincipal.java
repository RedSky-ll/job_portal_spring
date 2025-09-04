package ir.parsakav.jobportal.auth.security;

import ir.parsakav.jobportal.accounts.domain.*;
import lombok.Getter;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
public class UserPrincipal implements UserDetails {
    private final Long id;
    private final String email;
    private final String password;
    private final Role role;
    private final boolean enabled;

    public UserPrincipal(User u) {
        this.id = u.getId();
        this.email = u.getEmail();
        this.password = u.getPassword();
        this.role = u.getRole();
        this.enabled = u.isEnabled();
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}
