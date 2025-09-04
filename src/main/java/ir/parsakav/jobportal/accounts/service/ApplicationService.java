package ir.parsakav.jobportal.accounts.service;

import ir.parsakav.jobportal.accounts.domain.Application;
import ir.parsakav.jobportal.accounts.domain.Job;
import ir.parsakav.jobportal.accounts.domain.User;
import ir.parsakav.jobportal.accounts.repo.UserRepository;
import ir.parsakav.jobportal.jobs.dto.ApplicationDtos;
import ir.parsakav.jobportal.jobs.repo.ApplicationRepository;
import ir.parsakav.jobportal.jobs.repo.JobRepository;
import ir.parsakav.jobportal.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public Application apply(Long jobId, Long seekerId, ApplicationDtos.ApplyRequest req) throws BadRequestException {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        if(Boolean.FALSE.equals(job.isApproved())){
            throw new IllegalArgumentException("Job is not approved yet.");
        }
        if(applicationRepository.existsByJobIdAndSeekerId(jobId, seekerId)){
            throw new IllegalArgumentException("You have already applied to this job.");
        }
        User seeker = userRepository.findById(seekerId)
                .orElseThrow(() -> new IllegalArgumentException("Seeker not found"));


        Application a = Application.builder()
                .job(job)
                .seeker(seeker)
                .coverLetter(req.getCoverLetter())
                .resumePathOverride(req.getResumePathOverride())
                .status(Application.Status.SUBMITTED)
                .build();

        if(req.getResumePathOverride() == null || req.getResumePathOverride().isEmpty()){
a.setResumePathOverride(seeker.getProfile().getResumePath());
        }
        a = applicationRepository.save(a);

        // نوتیف به کارفرما: اپلیکیشن جدید
        String title = "درخواست جدید برای آگهی شما";
        String body  = "کاربر " + seeker.getEmail() + " برای آگهی «" + job.getTitle() + "» اپلای کرد.";
        notificationService.push(job.getEmployer().getId(), title, body);

        return a;
    }

    // لیست اپلیکیشن‌های کاربر (Seeker)
    @Transactional(readOnly = true)
    public Page<Application> myApplications(Long seekerId, Pageable pageable){
        return applicationRepository.findBySeekerId(seekerId, pageable);
    }

    // لیست اپلیکیشن‌های یک آگهی برای کارفرما
    @Transactional(readOnly = true)
    public Page<Application> employerListForJob(Long employerId, Long jobId, Pageable pageable){
        return applicationRepository.findByJobIdAndJobEmployerId(jobId, employerId, pageable);
    }

    // تصمیم کارفرما
    @Transactional
    public Application decide(Long employerId, Long applicationId, ApplicationDtos.DecisionRequest req) throws BadRequestException {
        // فقط ACCEPTED/REJECTED مجاز است
        if(req.getStatus() == null ||
                !(req.getStatus().equals(Application.Status.ACCEPTED) || req.getStatus().equals(Application.Status.REJECTED))){
            throw new IllegalArgumentException("Status must be ACCEPTED or REJECTED.");
        }

        Application a = applicationRepository.findByIdAndJobEmployerId(applicationId, employerId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found or not owned by you"));

        if(a.getStatus() == Application.Status.ACCEPTED || a.getStatus() == Application.Status.REJECTED){
            throw new IllegalArgumentException("This application has already been finalized.");
        }

        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new IllegalArgumentException("Employer not found"));

        a.setStatus(req.getStatus());
        a.setDecisionReason(req.getReason());
        a.setDecidedBy(employer);
        a.setDecidedAt(Instant.now());

        // حالت میانی اگر خواستی: REVIEWED
        // اگر لازم بود قبل از تصمیم نهایی هم استفاده شود.
        // اینجا مستقیم نهایی می‌کنیم.

        applicationRepository.save(a);

        // نوتیف به کارجو: نتیجه تصمیم
        String title = req.getStatus()==Application.Status.ACCEPTED ? "درخواست شما پذیرفته شد" : "درخواست شما رد شد";
        String body  = "نتیجه برای آگهی «" + a.getJob().getTitle() + "»: " + title +
                (req.getReason()!=null && !req.getReason().isBlank() ? (" — دلیل: " + req.getReason()) : "");
        notificationService.push(a.getSeeker().getId(), title, body);

        return a;
    }
}
