//package org.example.service.impl;
//
//import lombok.RequiredArgsConstructor;
//import org.example.model.Conversation;
//import org.example.model.MessageChat;
//import org.example.model.User;
//import org.example.repository.ConversationRepository;
//import org.example.repository.MessageChatRepository;
//import org.example.service.ConversationService;
//import org.example.service.UserService;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ConversationServiceImpl implements ConversationService {
//
//    private final ConversationRepository conversationRepository;
//    private final MessageChatRepository messageChatRepository;
//    private final UserService userService;
//
//    @Override
//    public Conversation getOrCreateConversation(int user1Id, int user2Id) {
//        return conversationRepository.findExistingConversation(user1Id, user2Id)
//                .orElseGet(() -> {
//                    Conversation conv = new Conversation();
//
//                    User u1 = userService.findById(user1Id)
//                            .orElseThrow(() -> new RuntimeException("User 1 not found"));
//                    User u2 = userService.findById(user2Id)
//                            .orElseThrow(() -> new RuntimeException("User 2 not found"));
//                    conv.setUser1(u1);
//                    conv.setUser2(u2);
//                    conv.setCreatedAt(LocalDate.now());
//
//                    return conversationRepository.save(conv);
//                });
//    }
//
//    @Override
//    public List<Conversation> findAllMyConversations(int userId) {
//        return conversationRepository.findAllByUserId(userId);
//    }
//
//    @Override
//    public MessageChat sendMessage(MessageChat message) {
//        message.setCreatedAt(LocalDateTime.now());
//        message.setRead(false);
//        return messageChatRepository.save(message);
//    }
//
//    @Override
//    public List<MessageChat> getMessagesByConversationId(int conversationId) {
//        return messageChatRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId);
//    }
//}
