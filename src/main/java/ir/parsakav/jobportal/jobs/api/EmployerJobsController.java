package ir.parsakav.jobportal.jobs.api;

import ir.parsakav.jobportal.accounts.domain.Job;
import ir.parsakav.jobportal.auth.security.UserPrincipal;
import ir.parsakav.jobportal.jobs.dto.JobForm;
import ir.parsakav.jobportal.jobs.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/employer/jobs")
@RequiredArgsConstructor
public class EmployerJobsController {
    private final JobService jobService;

    @PostMapping
    public Job create(@AuthenticationPrincipal UserPrincipal me, @Valid @RequestBody JobForm form) {
        return jobService.create(me.getId(), form);
    }

    @PutMapping("/{id}")
    public Job update(@AuthenticationPrincipal UserPrincipal me, @PathVariable Long id,
                      @Valid @RequestBody JobForm form) {
        return jobService.update(me.getId(), id, form);
    }

    @GetMapping
    public Page<Job> mine(@AuthenticationPrincipal UserPrincipal me,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size) {
        return jobService.listMine(me.getId(), page, size);
    }
}
