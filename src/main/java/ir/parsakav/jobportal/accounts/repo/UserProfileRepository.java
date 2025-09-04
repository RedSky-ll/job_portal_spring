package ir.parsakav.jobportal.accounts.repo;

// com/example/sjp/accounts/repo/UserProfileRepository.java

import ir.parsakav.jobportal.accounts.domain.UserProfile;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Query(value = "from UserProfile  up join fetch up.user where up.user.id=:userId",nativeQuery = false)
    Optional<UserProfile> findByUserId(Long userId);
}
