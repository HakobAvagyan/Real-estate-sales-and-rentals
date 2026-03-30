package org.example.service;

import org.example.dto.notification.NotificationRequestDto;
import org.example.dto.notification.NotificationResponseDto;
import org.example.model.User;

import java.util.List;

public interface NotificationService {

    NotificationRequestDto findById(Integer id, Integer userId);

    void save(NotificationResponseDto createNotificationResponseDto);

    List<NotificationRequestDto> getAllNotificationsByUserId(Integer userId);

    void deleteById(Integer id,Integer userId);

    void notifyUserUnblocked(User user);

    void notifyUserBlocked(User user);
}
