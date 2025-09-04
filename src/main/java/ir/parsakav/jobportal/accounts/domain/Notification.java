package ir.parsakav.jobportal.accounts.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

// notifications/domain/Notification.java
@Entity
@Data
@Builder @NoArgsConstructor @AllArgsConstructor
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @ManyToOne @JoinColumn(name="user_id") User user;
    String title; String body;
    boolean readFlag;
    Instant createdAt;
}

// logs/domain/ActionLog.java
