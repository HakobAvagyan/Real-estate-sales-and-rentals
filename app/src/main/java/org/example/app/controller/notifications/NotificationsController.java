package org.example.app.controller.notifications;

import lombok.AllArgsConstructor;
import org.example.dto.notification.NotificationRequestDto;
import org.example.dto.notification.NotificationResponseDto;
import org.example.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@AllArgsConstructor
public class NotificationsController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public String getNotifications(ModelMap modelMap) {
        List<NotificationResponseDto> allNotificationsByUserId = notificationService.getAllNotificationsByUserId();
        modelMap.addAttribute("notifications", allNotificationsByUserId);
        return "notification/notification";
    }

    @GetMapping("/notifications/{id}")
    public String getOwnNotifications(@PathVariable int id, ModelMap modelMap) {
        NotificationRequestDto byId = notificationService.findById(id);
        modelMap.addAttribute("notification", byId);
        return "notification/ownNotification";
    }

    @GetMapping("/notifications/{id}/delete")
    public String removeNotification(@PathVariable int id) {
        notificationService.deleteById(id);
        return "redirect:/notifications";
    }

}
