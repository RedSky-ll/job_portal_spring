package ir.parsakav.jobportal.accounts.api;

import ir.parsakav.jobportal.accounts.domain.Application;
import ir.parsakav.jobportal.accounts.service.ApplicationService;
import ir.parsakav.jobportal.auth.security.UserPrincipal;
import ir.parsakav.jobportal.jobs.dto.ApplicationDtos;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ApplicationController {

    private final ApplicationService applicationService;

    private static UserPrincipal me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal up)) {
            throw new IllegalStateException("Authenticated principal not found");
        }
        return up;
    }

    // اپلای به آگهی
    @PreAuthorize("hasRole('SEEKER')")
    @PostMapping("/jobs/{jobId}/apply")
    public ResponseEntity<ApplicationDtos.ApplicationResponse> apply(
            @PathVariable Long jobId,
            @RequestBody ApplicationDtos.ApplyRequest req,
            Authentication authentication) throws BadRequestException {

        UserPrincipal up = me(authentication);
        Application a = applicationService.apply(jobId, up.getId(), req);
        return ResponseEntity.ok(ApplicationDtos.ApplicationResponse.from(a));
    }

    // لیست اپلیکیشن‌های خود کارجو
    @PreAuthorize("hasRole('SEEKER')")
    @GetMapping("/me/applications")
    public ResponseEntity<Page<ApplicationDtos.ApplicationResponse>> myApplications(
            Pageable pageable,
            Authentication authentication) {

        UserPrincipal up = me(authentication);
        Page<ApplicationDtos.ApplicationResponse> page =
                applicationService.myApplications(up.getId(), pageable)
                        .map(ApplicationDtos.ApplicationResponse::from);
        return ResponseEntity.ok(page);
    }
}
