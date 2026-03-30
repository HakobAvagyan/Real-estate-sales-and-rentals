package org.example.app.controller.notifications;

import lombok.AllArgsConstructor;
import org.example.dto.notification.NotificationRequestDto;
import org.example.dto.notification.NotificationResponseDto;
import org.example.mapper.user.UserRegisterMapper;
import org.example.model.Notification;
import org.example.model.User;
import org.example.service.NotificationService;
import org.example.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@AllArgsConstructor
public class NotificationsController {

    private final NotificationService notificationService;
    private final UserService userService;
    private final UserRegisterMapper userRegisterMapper;

    @GetMapping("/notifications")
    public String getNotifications(ModelMap modelMap) {
        List<NotificationRequestDto> allNotificationsByUserId = notificationService.getAllNotificationsByUserId(getCurrentUser().getId());
        modelMap.addAttribute("notifications", allNotificationsByUserId);
        return "notification/notification";
    }

    @GetMapping("/notifications/{id}")
    public String getOwnNotifications(@PathVariable int id, ModelMap modelMap) {
        NotificationRequestDto byId = notificationService.findById(id, getCurrentUser().getId());
        modelMap.addAttribute("notification", byId);
        return "notification/ownNotification";
    }

    @GetMapping("/notifications/{id}/delete")
    public String removeNotification(@PathVariable int id) {
        notificationService.deleteById(id,getCurrentUser().getId());
        return "redirect:/notifications";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("User not authenticated");
        }

        String email = auth.getName();
        return userService.findByEmail(email)
                .map(userRegisterMapper::toUser).orElse(null);
    }
}
