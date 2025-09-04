package ir.parsakav.jobportal.jobs.service;


import ir.parsakav.jobportal.accounts.domain.Application;
import ir.parsakav.jobportal.accounts.domain.Job;
import ir.parsakav.jobportal.accounts.domain.User;
import ir.parsakav.jobportal.accounts.repo.UserRepository;


import ir.parsakav.jobportal.accounts.service.ApplicationService;
import ir.parsakav.jobportal.jobs.dto.ApplicationDtos;
import ir.parsakav.jobportal.jobs.repo.ApplicationRepository;
import ir.parsakav.jobportal.jobs.repo.JobRepository;
import ir.parsakav.jobportal.notifications.service.NotificationService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

    ApplicationRepository appRepo = mock(ApplicationRepository.class);
    JobRepository jobRepo = mock(JobRepository.class);
    UserRepository userRepo = mock(UserRepository.class);
    NotificationService notification = mock(NotificationService.class);

    ApplicationService service;

    User seeker, employer;
    Job job;

    @BeforeEach
    void setUp() {
        service = new ApplicationService(appRepo, jobRepo, userRepo, notification);

        employer = new User(); employer.setId(100L); employer.setEmail("emp@x.com");
        seeker   = new User(); seeker.setId(200L);   seeker.setEmail("seek@x.com");

        job = new Job();
        job.setId(10L);
        job.setTitle("Java Dev");
        job.setEmployer(employer);
        job.setApproved(true);
    }

    @Test
    void apply_ok_sendsNotification() throws BadRequestException {
        when(jobRepo.findById(10L)).thenReturn(Optional.of(job));
        when(appRepo.existsByJobIdAndSeekerId(10L, 200L)).thenReturn(false);
        when(userRepo.findById(200L)).thenReturn(Optional.of(seeker));
        when(appRepo.save(any(Application.class))).thenAnswer(i -> {
            Application a = i.getArgument(0);
            a.setId(999L);
            return a;
        });

        var req = new ApplicationDtos.ApplyRequest();
        req.setCoverLetter("Hi!");
        var res = service.apply(10L, 200L, req);

        assertThat(res.getId()).isEqualTo(999L);
        assertThat(res.getJob().getId()).isEqualTo(10L);
        assertThat(res.getSeeker().getId()).isEqualTo(200L);
        assertThat(res.getStatus()).isEqualTo(Application.Status.SUBMITTED);

        verify(notification).push(eq(100L), anyString(), contains("seek@x.com"));
    }

    @Test
    void apply_fails_if_job_not_found() {
        when(jobRepo.findById(10L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.apply(10L, 200L, new ApplicationDtos.ApplyRequest()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void apply_fails_if_job_not_approved() {
        job.setApproved(false);
        when(jobRepo.findById(10L)).thenReturn(Optional.of(job));
        assertThatThrownBy(() -> service.apply(10L, 200L, new ApplicationDtos.ApplyRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not approved");
    }

    @Test
    void apply_fails_if_duplicate() {
        when(jobRepo.findById(10L)).thenReturn(Optional.of(job));
        when(appRepo.existsByJobIdAndSeekerId(10L, 200L)).thenReturn(true);
        assertThatThrownBy(() -> service.apply(10L, 200L, new ApplicationDtos.ApplyRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already applied");
    }

    @Test
    void decide_accept_ok_and_notifies_seeker() throws BadRequestException {
        Application a = new Application();
        a.setId(999L);
        a.setJob(job);
        a.setSeeker(seeker);
        a.setStatus(Application.Status.SUBMITTED);

        when(appRepo.findByIdAndJobEmployerId(999L, 100L)).thenReturn(Optional.of(a));
        when(userRepo.findById(100L)).thenReturn(Optional.of(employer));

        var req = new ApplicationDtos.DecisionRequest();
        req.setStatus(Application.Status.ACCEPTED);
        req.setReason("Great CV");

        var res = service.decide(100L, 999L, req);

        assertThat(res.getStatus()).isEqualTo(Application.Status.ACCEPTED);
        assertThat(res.getDecisionReason()).isEqualTo("Great CV");
        assertThat(res.getDecidedBy().getId()).isEqualTo(100L);
        assertThat(res.getDecidedAt()).isNotNull();

        verify(notification).push(eq(200L), contains("پذیرفته"), contains("Great CV"));

        // همچنین باید ذخیره شود
        ArgumentCaptor<Application> cap = ArgumentCaptor.forClass(Application.class);
        verify(appRepo).save(cap.capture());
        assertThat(cap.getValue().getStatus()).isEqualTo(Application.Status.ACCEPTED);
    }

    @Test
    void decide_reject_requires_status_and_ownership() {
        // not found / ownership
        when(appRepo.findByIdAndJobEmployerId(1L, 100L)).thenReturn(Optional.empty());
        var bad = new ApplicationDtos.DecisionRequest();
        bad.setStatus(Application.Status.REJECTED);
        assertThatThrownBy(() -> service.decide(100L, 1L, bad))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
