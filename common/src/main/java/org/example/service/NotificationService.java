package org.example.service;

import org.example.dto.notification.NotificationDto;
import org.example.model.Notification;
import org.example.model.User;

import java.util.List;

public interface NotificationService {

    NotificationDto findById(Integer id);

    void save(NotificationDto createNotificationDto);

    List<Notification> getAllNotificationsByUserId(Integer userId);

    void deleteById(Integer id);

    void notifyUserUnblocked(User user);

    void notifyUserBlocked(User user);
}
