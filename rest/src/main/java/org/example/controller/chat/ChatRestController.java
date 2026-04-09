package org.example.controller.chat;

import lombok.RequiredArgsConstructor;
import org.example.dto.chat.ChatConversationDto;
import org.example.dto.chat.ChatMessageDto;
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
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ConversationService conversationService;
    private final UserService userService;

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
            @RequestBody Map<String, String> body,
            Principal principal) {
        String text = body == null ? null : body.get("text");
        int uid = resolveUserId(principal);
        return conversationService.sendMessage(conversationId, uid, text);
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
        return userService.getIdByEmail(principal.getName());
    }
}
