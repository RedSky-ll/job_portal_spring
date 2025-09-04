package ir.parsakav.jobportal.accounts.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

// jobs/domain/Job.java
@Entity
@Data
@Builder @NoArgsConstructor @AllArgsConstructor
public class Job {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @ManyToOne @JoinColumn(name="employer_id") User employer; // role=EMPLOYER
    @Column(nullable=false) String title;
    @Column(nullable=false, length=4000) String description;
    String location;
    String employmentType; // FULL_TIME, PART_TIME, ...
    boolean approved;      // توسط ادمین
    @ElementCollection
    List<String> tags;
    Instant createdAt;
    Instant updatedAt;
}
