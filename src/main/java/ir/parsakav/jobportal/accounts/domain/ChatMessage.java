package ir.parsakav.jobportal.accounts.domain;

// chat/dto/ChatMessage.java

import lombok.Data;

@Data
public class ChatMessage {
    private String roomId;     // "chat.{min}.{max}"
    private Long fromUserId;
    private Long toUserId;
    private String body;
    private Long ts;           // optional
}
