package ir.parsakav.jobportal.jobs.service;

import ir.parsakav.jobportal.accounts.domain.Job;
import ir.parsakav.jobportal.jobs.repo.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class JobQueryService {
    private final JobRepository repo;

    @Cacheable(cacheNames = "jobs_home", key = "#page + '-' + #size + '-' + #q")
    public Page<Job> search(String q, int page, int size) {
        return repo.findByTitleContainingIgnoreCaseAndApprovedTrue(
                q, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Job getById(Long id) { return repo.findById(id).orElseThrow(); }
}
