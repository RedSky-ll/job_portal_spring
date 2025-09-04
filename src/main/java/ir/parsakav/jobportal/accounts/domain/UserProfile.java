package ir.parsakav.jobportal.accounts.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @OneToOne
    @JoinColumn(name="user_id") User user;
    String fullName;
    String headline;
    String location;
    String avatarPath;     // فایل اصلی
    String avatarThumbPath;// thumbnail
    String resumePath;     // PDF
}
