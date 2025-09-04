package ir.parsakav.jobportal.accounts.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

import ir.parsakav.jobportal.accounts.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "applications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "seeker_id"}))
public class Application {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="job_id", nullable=false)
    Job job;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="seeker_id", nullable=false)
    User seeker;

    @Lob
    String coverLetter;

    // اگر رزومه متفاوت آپلود شد (مسیر فایل)
    String resumePathOverride;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    Status status;

    // دلیل کارفرما برای ACCEPT/REJECT
    @Column(length = 2000)
    String decisionReason;

    // چه کسی و کی تصمیم گرفت
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="decided_by_id")
    User decidedBy;

    Instant createdAt;
    Instant decidedAt;

    public enum Status { SUBMITTED, REVIEWED, ACCEPTED, REJECTED }
    @PrePersist void onCreate(){ if(createdAt==null) createdAt = Instant.now(); if(status==null) status = Status.SUBMITTED; }
}

