package org.example.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.enums.ConversationType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationDto {
    private int id;
    private ConversationType conversationType;
    private int otherUserId;
    private String otherUserName;
    private String otherUserEmail;
    private Integer propertyId;
    private LocalDateTime lastMessageAt;
    private long unreadCount;
}
