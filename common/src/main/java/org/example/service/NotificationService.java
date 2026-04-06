package org.example.service;

import org.example.dto.notification.NotificationRequestDto;
import org.example.dto.notification.NotificationResponseDto;
import org.example.model.User;

import java.util.List;

public interface NotificationService {

    NotificationRequestDto findById(Integer id);

    void save(NotificationRequestDto NotificationRequestDto);

    List<NotificationResponseDto> getAllNotificationsByUserId();

    void deleteById(Integer id);

    void notifyUserUnblocked(User user);

    void notifyUserBlocked(User user);

}
