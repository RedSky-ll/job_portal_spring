package ir.parsakav.jobportal.jobs.repo;

import ir.parsakav.jobportal.accounts.domain.Notification;
import ir.parsakav.jobportal.accounts.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}
