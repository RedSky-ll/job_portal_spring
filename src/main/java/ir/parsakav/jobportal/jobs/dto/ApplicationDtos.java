package ir.parsakav.jobportal.jobs.dto;


import ir.parsakav.jobportal.accounts.domain.Application;
import lombok.*;

import java.time.Instant;

public class ApplicationDtos {

    @Data
    public static class ApplyRequest {
        String coverLetter;
        // اگر از قبل فایل رزومه را آپلود کرده‌ای و مسیرش را داری (اختیاری):
        String resumePathOverride;
    }

    @Data
    public static class DecisionRequest {
        // ACCEPTED یا REJECTED (فقط همین دوتا)
        Application.Status status;
        String reason;
    }

    @Builder @Data
    public static class ApplicationResponse {
        Long id;
        Long jobId;
        Long seekerId;
        String seekerEmail;
        Application.Status status;
        String decisionReason;
        Instant createdAt;
        Instant decidedAt;

        public static ApplicationResponse from(Application a){
            return ApplicationResponse.builder()
                    .id(a.getId())
                    .jobId(a.getJob().getId())
                    .seekerId(a.getSeeker().getId())
                    .seekerEmail(a.getSeeker().getEmail())
                    .status(a.getStatus())
                    .decisionReason(a.getDecisionReason())
                    .createdAt(a.getCreatedAt())
                    .decidedAt(a.getDecidedAt())
                    .build();
        }
    }
}
