package ir.parsakav.jobportal.accounts.service;

import ir.parsakav.jobportal.accounts.domain.User;
import ir.parsakav.jobportal.accounts.domain.UserProfile;
import ir.parsakav.jobportal.accounts.dto.UserProfileDto;
import ir.parsakav.jobportal.accounts.repo.UserProfileRepository;
import ir.parsakav.jobportal.accounts.repo.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class ProfileService {
    private final UserRepository users;
    private final UserProfileRepository profiles;
    @Transactional
    public UserProfileDto updateBasic(Long userId, String fullName, String headline, String location) {
        User u = users.findById(userId).orElseThrow();
        UserProfile p = u.getProfile();
        if (p == null) { p = UserProfile.builder().user(u).build(); }
        p.setFullName(fullName);
        p.setHeadline(headline);
        p.setLocation(location);
        profiles.save(p);
        u.setProfile(p);
        return toDto(p);
    }
    public UserProfileDto updateResume(Long userId, String path) {
        var p = users.findById(userId).orElseThrow();
        p.getProfile().setResumePath(path);
        users.save(p);
        return toDto(p.getProfile());
    }

    @Transactional
    public UserProfileDto updateAvatar(Long userId, String avatar, String thumb) {
        User u = users.findById(userId).orElseThrow();
        UserProfile p = u.getProfile();
        if (p == null) {
            p = UserProfile.builder().user(u).build();
        }
        p.setAvatarPath(avatar);
        p.setAvatarThumbPath(thumb);
        profiles.save(p);
        u.setProfile(p);
        return toDto(p);
    }

    public UserProfileDto getMe(Long userId) {
        User u = users.findById(userId).orElseThrow();
        return u.getProfile() == null ? null : toDto(u.getProfile());
    }
    @Transactional
    public UserProfileDto getOrCreate(Long userId) {
        User u = users.findById(userId).orElseThrow();
        UserProfile p = u.getProfile();
        if (p == null) {
            p = UserProfile.builder().user(u).build();
            profiles.save(p);
            u.setProfile(p);
        }
        return toDto(p);
    }
    private UserProfileDto toDto(UserProfile p) {
        return UserProfileDto.builder()
                .id(p.getId())
                .fullName(p.getFullName())
                .headline(p.getHeadline())
                .location(p.getLocation())
                .avatarPath(p.getAvatarPath())
                .avatarThumbPath(p.getAvatarThumbPath())
                .resumePath(p.getResumePath())
                .build();
    }
    public Long userIdByEmail(String email) {
        return users.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email))
                .getId();
    }


}
