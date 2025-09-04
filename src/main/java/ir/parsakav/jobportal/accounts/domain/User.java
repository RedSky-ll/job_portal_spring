package ir.parsakav.jobportal.accounts.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data @Builder @NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @Column(unique = true, nullable = false) String email;
    @Column(nullable = false) String password;
    @Enumerated(EnumType.STRING) Role role; // SEEKER, EMPLOYER, ADMIN
    boolean enabled;           // بعد از email-verify true می‌شود
    boolean emailVerified;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    UserProfile profile;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", enabled=" + enabled +
                ", emailVerified=" + emailVerified +
                '}';
    }
}

// accounts/domain/Role.java

// accounts/domain/UserProfile.java
