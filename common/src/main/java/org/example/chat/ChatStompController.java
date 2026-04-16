package org.example.chat;

import lombok.RequiredArgsConstructor;
import org.example.dto.chat.SendChatMessagePayload;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.ConversationService;
import org.example.service.security.SpringUser;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final ConversationService conversationService;
    private final UserRepository userRepository;

    @MessageMapping("/chat.send")
    public void send(SendChatMessagePayload payload, Principal principal) {
        int userId = resolveUserId(principal);
        conversationService.sendMessage(payload.getConversationId(), userId, payload.getText());
    }

    private int resolveUserId(Principal principal) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }
        if (principal instanceof org.springframework.security.core.Authentication authentication) {
            if (authentication.getPrincipal() instanceof SpringUser springUser) {
                return springUser.getUser().getId();
            }
            String email = authentication.getName();
            return userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_BY_EMAIL, email));
        }
        throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
    }
}
