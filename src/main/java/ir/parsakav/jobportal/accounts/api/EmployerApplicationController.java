package ir.parsakav.jobportal.accounts.api;


import ir.parsakav.jobportal.accounts.domain.Application;
import ir.parsakav.jobportal.accounts.service.ApplicationService;
import ir.parsakav.jobportal.auth.security.UserPrincipal;
import ir.parsakav.jobportal.jobs.dto.ApplicationDtos;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employer")
public class EmployerApplicationController {

    private final ApplicationService applicationService;

    private static UserPrincipal me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal up)) {
            throw new IllegalStateException("Authenticated principal not found");
        }
        return up;
    }

    // لیست اپلیکیشن‌های یک آگهی (برای کارفرما)
    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<Page<ApplicationDtos.ApplicationResponse>> listForJob(
            @PathVariable Long jobId,
            Pageable pageable,
            Authentication authentication) {

        UserPrincipal up = me(authentication);
        Page<ApplicationDtos.ApplicationResponse> page =
                applicationService.employerListForJob(up.getId(), jobId, pageable)
                        .map(ApplicationDtos.ApplicationResponse::from);
        return ResponseEntity.ok(page);
    }

    // تصمیم‌گیری (تأیید/رد) + دلیل
    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/applications/{applicationId}/decision")
    public ResponseEntity<ApplicationDtos.ApplicationResponse> decide(
            @PathVariable Long applicationId,
            @RequestBody ApplicationDtos.DecisionRequest req,
            Authentication authentication) throws BadRequestException {

        UserPrincipal up = me(authentication);
        Application a = applicationService.decide(up.getId(), applicationId, req);
        return ResponseEntity.ok(ApplicationDtos.ApplicationResponse.from(a));
    }
}
