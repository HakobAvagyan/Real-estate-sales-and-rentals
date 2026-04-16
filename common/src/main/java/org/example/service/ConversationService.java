package org.example.service;

import org.example.dto.chat.ChatConversationDto;
import org.example.dto.chat.ChatMessageDto;

import java.util.List;

public interface ConversationService {

    ChatConversationDto createOrGetDirect(int currentUserId, int otherUserId, Integer propertyId);

    ChatConversationDto createOrGetSupport(int currentUserId);

    List<ChatConversationDto> listMyConversations(int currentUserId);

    List<ChatMessageDto> getMessages(int conversationId, int currentUserId);

    ChatMessageDto sendMessage(int conversationId, int senderUserId, String text);

    void markConversationRead(int conversationId, int readerUserId);
}
