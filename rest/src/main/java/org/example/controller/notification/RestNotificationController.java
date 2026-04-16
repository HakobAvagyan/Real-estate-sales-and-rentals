package org.example.controller.notification;

import lombok.RequiredArgsConstructor;
import org.example.dto.notification.NotificationRequestDto;
import org.example.dto.notification.NotificationResponseDto;
import org.example.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class RestNotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponseDto> getNotifications() {
        return notificationService.getAllNotificationsByUserId();
    }

    @GetMapping("/{id}")
    public NotificationRequestDto getNotificationById(@PathVariable int id) {
        return notificationService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(@PathVariable int id) {
        notificationService.deleteById(id);
    }
}