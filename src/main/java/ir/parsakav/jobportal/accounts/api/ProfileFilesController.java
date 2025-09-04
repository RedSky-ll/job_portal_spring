package ir.parsakav.jobportal.accounts.api;

// ir/parsakav/jobportal/accounts/api/ProfileFilesController.java

import ir.parsakav.jobportal.auth.security.UserPrincipal;
import ir.parsakav.jobportal.accounts.repo.UserProfileRepository;
import ir.parsakav.jobportal.core.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me/files")
@RequiredArgsConstructor
public class ProfileFilesController {
    private final UserProfileRepository profiles;
    private final FileStorageService storage;

    @GetMapping("/resume")
    public ResponseEntity<Resource> downloadResume(@AuthenticationPrincipal UserPrincipal me) {
        var p = profiles.findByUserId(me.getId()).orElseThrow();
        if (p.getResumePath() == null) return ResponseEntity.notFound().build();

        Resource res = storage.loadAsResource(p.getResumePath()); // خودت پیاده کردی؛ از disk بخون
        String filename = storage.filename(p.getResumePath());

        return ResponseEntity.ok()
                .contentType(p.getResumePath().toLowerCase().endsWith(".pdf") ? MediaType.APPLICATION_PDF : MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(res);
    }
}

