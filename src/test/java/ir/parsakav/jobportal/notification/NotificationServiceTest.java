package ir.parsakav.jobportal.notification;


import ir.parsakav.jobportal.accounts.domain.Notification;
import ir.parsakav.jobportal.accounts.domain.User;

import ir.parsakav.jobportal.jobs.repo.NotificationRepository;
import ir.parsakav.jobportal.notifications.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Test
    void notifyUser_saves_and_sends_ws() {
        var repo = mock(NotificationRepository.class);
        var stomp = mock(SimpMessagingTemplate.class);

        var user = new User(); user.setId(200L);
        var saved = Notification.builder().id(1L).user(user).title("t").body("b").build();

        when(repo.save(any(Notification.class))).thenReturn(saved);

        var svc = new NotificationService(repo, stomp);
        svc.push(200L, "سلام", "یک پیام");

        verify(repo).save(any(Notification.class));
        verify(stomp).convertAndSend(Optional.ofNullable(eq("/topic/notifications.200")), any());
    }
}
