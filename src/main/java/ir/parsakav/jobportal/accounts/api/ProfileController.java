package ir.parsakav.jobportal.accounts.api;

import ir.parsakav.jobportal.accounts.dto.UserProfileDto;
import ir.parsakav.jobportal.accounts.service.ProfileService;
import ir.parsakav.jobportal.auth.security.UserPrincipal;
import ir.parsakav.jobportal.core.storage.FileStorageService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnails;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final FileStorageService storage;
    // نمونه در ProfileController
    @PostMapping(value="/avatar", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserProfileDto uploadAvatar(@AuthenticationPrincipal UserPrincipal me,
                                       @RequestPart("file") MultipartFile file) throws IOException {
        String webPath = storage.save(file, "avatars"); // <-- /uploads/avatars/uuid_name.jpg
        String thumbWebPath = webPath.replaceFirst("(\\.[^.]+)$", "_thumb$1");
        // تولید thumbnail روی دیسک:
        Thumbnails.of(storage.resolvePhysical(webPath).toFile())
                .size(256,256)
                .toFile(storage.resolvePhysical(thumbWebPath).toFile());

        return profileService.updateAvatar(me.getId(), webPath, thumbWebPath);
    }

    @PostMapping(value="/resume", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserProfileDto uploadResume(@AuthenticationPrincipal UserPrincipal me,
                                       @RequestPart("file") MultipartFile file) throws IOException {
        String webPath = storage.save(file, "resumes"); // <-- /uploads/resumes/uuid_name.pdf
        return profileService.updateResume(me.getId(), webPath);
    }


    @GetMapping("/profile")
    public UserProfileDto me(@AuthenticationPrincipal UserPrincipal me) {
        System.out.println("Amir");
        System.out.println(me.toString());
        return profileService.getOrCreate(me.getId());
    }

    @PutMapping("/profile")
    public UserProfileDto update(@AuthenticationPrincipal UserPrincipal me,
                                 @RequestBody UserProfileDto dto) {
        return profileService.updateBasic(me.getId(), dto.getFullName(), dto.getHeadline(), dto.getLocation());
    }

}
