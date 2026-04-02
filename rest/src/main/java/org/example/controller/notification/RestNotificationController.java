package org.example.controller.notification;

import lombok.RequiredArgsConstructor;
import org.example.dto.notification.NotificationRequestDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.mapper.user.UserRegisterMapper;
import org.example.model.User;
import org.example.service.NotificationService;
import org.example.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class RestNotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    private final UserRegisterMapper userRegisterMapper;

    @GetMapping
    public List<NotificationRequestDto> getNotifications() {
        return notificationService.getAllNotificationsByUserId(getUser().getId());
    }

    @GetMapping("/{id}")
    public NotificationRequestDto getNotificationById(@PathVariable int id) {
        return notificationService.findById(id, getUser().getId());
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable int id) {
        notificationService.deleteById(id, getUser().getId());
    }

    private User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }

        String email = auth.getName();
        return userService.findByEmail(email)
                .map(userRegisterMapper::toUser).orElse(null);
    }
}
