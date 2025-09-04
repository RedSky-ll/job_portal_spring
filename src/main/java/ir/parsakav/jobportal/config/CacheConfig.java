package ir.parsakav.jobportal.config;

import ir.parsakav.jobportal.accounts.domain.Job;
import ir.parsakav.jobportal.jobs.repo.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

// config/CacheConfig.java
@Configuration
@EnableCaching
public class CacheConfig { }

