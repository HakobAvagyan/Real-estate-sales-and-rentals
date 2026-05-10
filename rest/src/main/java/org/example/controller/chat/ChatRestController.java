package org.example.controller.chat;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.dto.chat.ChatConversationDto;
import org.example.dto.chat.ChatMessageDto;
import org.example.dto.chat.SendChatMessagePayload;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.service.ConversationService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ConversationService conversationService;
    private final UserService userService;
    private final ChatMessageJsonConverter chatMessageJsonConverter;

    public ChatRestController(
            ConversationService conversationService,
            UserService userService,
            ChatMessageJsonConverter chatMessageJsonConverter) {
        this.conversationService = conversationService;
        this.userService = userService;
        this.chatMessageJsonConverter = chatMessageJsonConverter;
    }

    @PostMapping("/conversations/direct/{otherUserId}")
    public ChatConversationDto createDirect(
            @PathVariable int otherUserId,
            @RequestParam(required = false) Integer propertyId,
            Principal principal) {
        int uid = resolveUserId(principal);
        return conversationService.createOrGetDirect(uid, otherUserId, propertyId);
    }

    @PostMapping("/conversations/support")
    public ChatConversationDto createSupport(Principal principal) {
        int uid = resolveUserId(principal);
        return conversationService.createOrGetSupport(uid);
    }

    @GetMapping("/conversations")
    public List<ChatConversationDto> list(Principal principal) {
        int uid = resolveUserId(principal);
        return conversationService.listMyConversations(uid);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<ChatMessageDto> messages(@PathVariable int conversationId, Principal principal) {
        int uid = resolveUserId(principal);
        return conversationService.getMessages(conversationId, uid);
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ChatMessageDto send(
            @PathVariable int conversationId,
            @RequestBody JsonNode body,
            Principal principal) {
        SendChatMessagePayload payload = chatMessageJsonConverter.convert(body, conversationId);
        int uid = resolveUserId(principal);
        return conversationService.sendMessage(payload.getConversationId(), uid, payload.getText());
    }

    @PostMapping("/conversations/{conversationId}/read")
    public void markRead(@PathVariable int conversationId, Principal principal) {
        int uid = resolveUserId(principal);
        conversationService.markConversationRead(conversationId, uid);
    }

    private int resolveUserId(Principal principal) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }
        return userService.findByEmail(principal.getName()).getId();
    }
}
