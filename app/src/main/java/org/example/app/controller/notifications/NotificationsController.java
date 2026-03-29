package org.example.app.controller.notifications;

import lombok.AllArgsConstructor;
import org.example.dto.notification.NotificationDto;
import org.example.model.Notification;
import org.example.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class NotificationsController {

    private final NotificationService notificationService;

    @GetMapping("/myNotification")
    public String getNotifications(@RequestParam("userId") int userId, ModelMap modelMap) {
        List<Notification> allNotificationsByUserId = notificationService.getAllNotificationsByUserId(userId);
        modelMap.addAttribute("notifications", allNotificationsByUserId);
        return "notification/notification";
    }

    @GetMapping("/myOwnNotification")
    public String getOwnNotifications(@RequestParam("id") int id, ModelMap modelMap) {
        NotificationDto byId = notificationService.findById(id);
        modelMap.addAttribute("notification", byId);
        return "notification/ownNotification";
    }

    @GetMapping("/removeMyNotification")
    public String removeNotification(@RequestParam("id") int id) {
        int userId = notificationService.findById(id).getUser().getId();
        notificationService.deleteById(id);
        return "redirect:/myNotification?userId=" + userId;
    }
}
