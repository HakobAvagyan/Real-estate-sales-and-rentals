package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chat.ChatPushService;
import org.example.dto.chat.ChatConversationDto;
import org.example.dto.chat.ChatMessageDto;
import org.example.dto.notification.NotificationRequestDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.model.Conversation;
import org.example.model.MessageChat;
import org.example.model.Property;
import org.example.model.User;
import org.example.model.enums.ConversationType;
import org.example.model.enums.Role;
import org.example.repository.ConversationRepository;
import org.example.repository.MessageChatRepository;
import org.example.repository.PropertyRepository;
import org.example.repository.UserRepository;
import org.example.service.ConversationService;
import org.example.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageChatRepository messageChatRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final ChatPushService chatPushService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ChatConversationDto createOrGetDirect(int currentUserId, int otherUserId, Integer propertyId) {
        if (currentUserId == otherUserId) {
            throw new BusinessException(ErrorCode.CANNOT_MESSAGE_SELF);
        }
        getUserOrThrow(currentUserId);
        getUserOrThrow(otherUserId);

        Property property = null;
        if (propertyId != null) {
            property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PROPERTY_NOT_FOUND, propertyId));
            if (property.getUser().getId() != otherUserId) {
                throw new BusinessException(ErrorCode.CONVERSATION_ACCESS_DENIED);
            }
        }

        int small = Math.min(currentUserId, otherUserId);
        int large = Math.max(currentUserId, otherUserId);

        Optional<Conversation> existing = propertyId == null
                ? conversationRepository.findByConversationTypeAndUser1IdAndUser2IdAndPropertyIsNull(
                ConversationType.DIRECT, small, large)
                : conversationRepository.findByConversationTypeAndUser1IdAndUser2IdAndPropertyId(
                ConversationType.DIRECT, small, large, propertyId);

        final Property propertyRef = property;
        Conversation conv = existing.orElseGet(() -> {
            Conversation c = new Conversation();
            c.setUser1(userRepository.getReferenceById(small));
            c.setUser2(userRepository.getReferenceById(large));
            c.setConversationType(ConversationType.DIRECT);
            c.setProperty(propertyRef);
            c.setCreatedAt(LocalDate.now());
            c.setUpdatedAt(LocalDateTime.now());
            return conversationRepository.save(c);
        });

        return toConversationDto(conv, currentUserId);
    }

    @Override
    @Transactional
    public ChatConversationDto createOrGetSupport(int currentUserId) {
        userRepository.findById(currentUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, currentUserId));

        Optional<Conversation> existing = conversationRepository.findSupportForClient(
                ConversationType.SUPPORT, currentUserId);
        if (existing.isPresent()) {
            return toConversationDto(existing.get(), currentUserId);
        }

        User manager = pickSupportAgent(currentUserId);

        User client = userRepository.getReferenceById(currentUserId);
        Conversation c = new Conversation();
        c.setUser1(client);
        c.setUser2(manager);
        c.setConversationType(ConversationType.SUPPORT);
        c.setProperty(null);
        c.setCreatedAt(LocalDate.now());
        c.setUpdatedAt(LocalDateTime.now());
        c = conversationRepository.save(c);
        return toConversationDto(c, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatConversationDto> listMyConversations(int currentUserId) {

        return conversationRepository.findAllByUserIdOrderByLastMessageAtDesc(currentUserId)
                .stream()
                .map(c -> toConversationDto(c, currentUserId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getMessages(int conversationId, int currentUserId) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONVERSATION_NOT_FOUND, conversationId));
        assertParticipant(conv, currentUserId);
        return messageChatRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(this::toMessageDto)
                .toList();
    }

    @Override
    @Transactional
    public ChatMessageDto sendMessage(int conversationId, int senderUserId, String text) {
        if (text == null || text.isBlank()) {
            throw new BusinessException(ErrorCode.TRY_AGAIN);
        }
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONVERSATION_NOT_FOUND, conversationId));
        assertParticipant(conv, senderUserId);

        User sender = userRepository.getReferenceById(senderUserId);
        MessageChat msg = new MessageChat();
        msg.setConversation(conv);
        msg.setUser(sender);
        msg.setText(text.trim());
        msg.setRead(false);
        msg.setCreatedAt(LocalDateTime.now());
        msg = messageChatRepository.save(msg);

        conv.setLastMessageAt(msg.getCreatedAt());
        conv.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conv);

        ChatMessageDto dto = toMessageDto(msg);
        User recipient = otherParticipant(conv, senderUserId);
        chatPushService.pushMessage(recipient.getEmail(), dto);
        chatPushService.pushMessage(sender.getEmail(), dto);

        ChatConversationDto inboxRow = toConversationDto(conv, recipient.getId());
        chatPushService.pushInboxRefresh(recipient.getEmail(), inboxRow);
        chatPushService.pushInboxRefresh(sender.getEmail(), toConversationDto(conv, senderUserId));

        NotificationRequestDto n = new NotificationRequestDto();
        n.setUserId(recipient.getId());
        n.setTitle("New message");
        n.setMessage("You have a new chat message from " + displayName(sender));
        notificationService.save(n);

        return dto;
    }

    @Override
    @Transactional
    public void markConversationRead(int conversationId, int readerUserId) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONVERSATION_NOT_FOUND, conversationId));
        assertParticipant(conv, readerUserId);
        messageChatRepository.markReadForOthers(conversationId, readerUserId);
    }

    private void assertParticipant(Conversation conv, int userId) {
        if (conv.getUser1().getId() != userId && conv.getUser2().getId() != userId) {
            throw new BusinessException(ErrorCode.CONVERSATION_ACCESS_DENIED);
        }
    }

    private User otherParticipant(Conversation conv, int userId) {
        if (conv.getUser1().getId() == userId) {
            return userRepository.findById(conv.getUser2().getId()).orElseThrow();
        }
        return userRepository.findById(conv.getUser1().getId()).orElseThrow();
    }

    private ChatConversationDto toConversationDto(Conversation c, int viewerUserId) {
        User other = c.getUser1().getId() == viewerUserId ? c.getUser2() : c.getUser1();
        long unread = messageChatRepository.countUnreadForUser(c.getId(), viewerUserId);
        return ChatConversationDto.builder()
                .id(c.getId())
                .conversationType(c.getConversationType())
                .otherUserId(other.getId())
                .otherUserName(displayName(other))
                .otherUserEmail(other.getEmail())
                .propertyId(c.getProperty() == null ? null : c.getProperty().getId())
                .lastMessageAt(c.getLastMessageAt())
                .unreadCount(unread)
                .build();
    }

    private ChatMessageDto toMessageDto(MessageChat m) {
        User sender = m.getUser();
        return ChatMessageDto.builder()
                .id(m.getId())
                .conversationId(m.getConversation().getId())
                .senderId(sender.getId())
                .senderName(displayName(sender))
                .text(m.getText())
                .read(m.isRead())
                .createdAt(m.getCreatedAt())
                .build();
    }

    private String displayName(User u) {
        String n = u.getName() == null ? "" : u.getName();
        String s = u.getSurname() == null ? "" : u.getSurname();
        return (n + " " + s).trim();
    }

    private User pickSupportAgent(int currentUserId) {
        return userRepository
                .findFirstByRoleInAndIdNot(List.of(Role.MANAGER), currentUserId)
                .or(() -> userRepository.findFirstByRoleInAndIdNot(List.of(Role.ADMIN), currentUserId))
                .orElseThrow(() -> new BusinessException(ErrorCode.NO_MANAGER_AVAILABLE));
    }


    private User getUserOrThrow(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
    }

}
