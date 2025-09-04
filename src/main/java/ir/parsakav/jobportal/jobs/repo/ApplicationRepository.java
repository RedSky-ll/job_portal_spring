package ir.parsakav.jobportal.jobs.repo;


import ir.parsakav.jobportal.accounts.domain.Application;
import ir.parsakav.jobportal.accounts.domain.Job;
import ir.parsakav.jobportal.accounts.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


import ir.parsakav.jobportal.accounts.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByJobIdAndSeekerId(Long jobId, Long seekerId);
    Page<Application> findBySeekerId(Long seekerId, Pageable pageable);

    // برای کارفرما: لیست اپلیکیشن‌های یک آگهی
    Page<Application> findByJobIdAndJobEmployerId(Long jobId, Long employerId, Pageable pageable);

    // برای اعتبارسنجی مالکیت قبل از تصمیم‌گیری
    Optional<Application> findByIdAndJobEmployerId(Long id, Long employerId);
}
