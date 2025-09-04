package ir.parsakav.jobportal.notifications.service;


import ir.parsakav.jobportal.accounts.domain.Notification;
import ir.parsakav.jobportal.accounts.domain.User;
import ir.parsakav.jobportal.jobs.repo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repo;
    private final SimpMessagingTemplate ws;

    public void push(Long userId, String title, String body) {
        Notification n = repo.save(Notification.builder()
                .user(User.builder().id(userId).build())
                .title(title).body(body).readFlag(false)
                .createdAt(Instant.now()).build());
        ws.convertAndSend("/topic/notifications."+userId, n);
    }
}
