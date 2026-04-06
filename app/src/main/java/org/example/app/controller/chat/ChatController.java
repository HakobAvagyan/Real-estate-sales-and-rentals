package org.example.app.controller.chat;

import lombok.RequiredArgsConstructor;
import org.example.model.User;
import org.example.service.ConversationService;
import org.example.service.security.SpringUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ConversationService conversationService;

    @GetMapping("/messages")
    public String inbox(
            @RequestParam(required = false) Integer conversationId,
            @AuthenticationPrincipal SpringUser springUser,
            Model model) {
        User user = springUser.getUser();
        model.addAttribute("conversations", conversationService.listMyConversations(user.getId()));
        model.addAttribute("currentUserId", user.getId());
        model.addAttribute("currentUserEmail", user.getEmail());
        if (conversationId != null) {
            conversationService.markConversationRead(conversationId, user.getId());
            model.addAttribute("messages", conversationService.getMessages(conversationId, user.getId()));
            model.addAttribute("selectedConversationId", conversationId);
        }
        return "messages/messages";
    }

    @GetMapping("/messages/open/direct")
    public String openDirect(
            @RequestParam int otherUserId,
            @RequestParam(required = false) Integer propertyId,
            @AuthenticationPrincipal SpringUser springUser) {
        var dto = conversationService.createOrGetDirect(springUser.getUser().getId(), otherUserId, propertyId);
        return "redirect:/messages?conversationId=" + dto.getId();
    }

    @GetMapping("/messages/open/support")
    public String openSupport(@AuthenticationPrincipal SpringUser springUser) {
        var dto = conversationService.createOrGetSupport(springUser.getUser().getId());
        return "redirect:/messages?conversationId=" + dto.getId();
    }

    @PostMapping("/messages/send")
    public String sendMessage(
            @RequestParam int conversationId,
            @RequestParam String text,
            @AuthenticationPrincipal SpringUser springUser) {
        conversationService.sendMessage(conversationId, springUser.getUser().getId(), text);
        return "redirect:/messages?conversationId=" + conversationId;
    }
}
