package ir.parsakav.jobportal.jobs.service;

import ir.parsakav.jobportal.accounts.domain.Job;
import ir.parsakav.jobportal.accounts.domain.User;
import ir.parsakav.jobportal.accounts.repo.UserRepository;
import ir.parsakav.jobportal.jobs.dto.JobForm;
import ir.parsakav.jobportal.jobs.repo.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service @RequiredArgsConstructor
public class JobService {
    private final JobRepository jobs;
    private final UserRepository users;

    @Transactional
    public Job create(Long employerId, JobForm f) {
        User employer = users.findById(employerId).orElseThrow();
        Job j = Job.builder()
                .employer(employer)
                .title(f.getTitle())
                .description(f.getDescription())
                .location(f.getLocation())
                .employmentType(f.getEmploymentType())
                .tags(f.getTags())
                .approved(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return jobs.save(j);
    }

    @Transactional
    public Job update(Long employerId, Long jobId, JobForm f) {
        Job j = jobs.findById(jobId).orElseThrow();
        if (!j.getEmployer().getId().equals(employerId)) throw new IllegalStateException("Not owner");
        j.setTitle(f.getTitle());
        j.setDescription(f.getDescription());
        j.setLocation(f.getLocation());
        j.setEmploymentType(f.getEmploymentType());
        j.setTags(f.getTags());
        j.setUpdatedAt(Instant.now());
        return jobs.save(j);
    }

    public Page<Job> listMine(Long employerId, int page, int size) {
        User employer = users.findById(employerId).orElseThrow();
        return jobs.findByEmployer(employer, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }
}
