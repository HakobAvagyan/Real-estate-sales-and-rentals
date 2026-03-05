package org.example.service;

import org.example.model.Conversation;
import org.example.model.MessageChat;

import java.util.List;

public interface ConversationService {
    Conversation getOrCreateConversation(int user1Id, int user2Id);

    List<Conversation> findAllMyConversations(int userId);

    MessageChat sendMessage(MessageChat message);

    List<MessageChat> getMessagesByConversationId(int conversationId);
}
