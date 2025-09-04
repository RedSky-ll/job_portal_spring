package ir.parsakav.jobportal.accounts.api;

// chat/ChatController.java

import ir.parsakav.jobportal.accounts.domain.ChatMessage;
import ir.parsakav.jobportal.accounts.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate ws;
    private final UserRepository users;

    private String roomIdFor(Long a, Long b) {
        long m = Math.min(a, b), M = Math.max(a, b);
        return "chat." + m + "." + M;
    }

    @MessageMapping("/chat.send") // فرانت به /app/chat.send می‌فرستد
    public void send(ChatMessage msg) {
        // اعتبارسنجی‌های سبک:
        if (msg.getFromUserId() == null || msg.getToUserId() == null) return;
        if (!roomIdFor(msg.getFromUserId(), msg.getToUserId()).equals(msg.getRoomId())) return;
        // (اختیاری) چک اینکه هر دو کاربر وجود دارند
        if (!(users.existsById(msg.getFromUserId()) && users.existsById(msg.getToUserId()))) return;

        if (msg.getBody() == null || msg.getBody().isBlank()) return;
        if (msg.getTs() == null) msg.setTs(System.currentTimeMillis());

        // انتشار برای هر دو سمت:
        ws.convertAndSend("/topic/" + msg.getRoomId(), msg);
    }
}
