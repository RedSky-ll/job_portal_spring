package ir.parsakav.jobportal.accounts.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class UserProfileDto {
    private Long id;
    private String fullName;
    private String headline;
    private String location;
    private String avatarPath;
    private String avatarThumbPath;
    private String resumePath;
}
