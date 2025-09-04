package ir.parsakav.jobportal.accounts.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Data
@Builder @NoArgsConstructor @AllArgsConstructor
public class ActionLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    Long userId;
    String method; String path; String ip;
    int status;
    Instant ts;
}
