package org.example.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatPushService {

    private final SimpMessagingTemplate messagingTemplate;

    public void pushMessage(String recipientEmail, Object payload) {
        messagingTemplate.convertAndSendToUser(recipientEmail, "/queue/chat-messages", payload);
    }

    public void pushInboxRefresh(String recipientEmail, Object inboxSummary) {
        messagingTemplate.convertAndSendToUser(recipientEmail, "/queue/chat-inbox", inboxSummary);
    }
}
