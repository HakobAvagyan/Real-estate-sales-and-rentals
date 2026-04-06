package org.example.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private int id;
    private int conversationId;
    private int senderId;
    private String senderName;
    private String text;
    private boolean read;
    private LocalDateTime createdAt;
}
