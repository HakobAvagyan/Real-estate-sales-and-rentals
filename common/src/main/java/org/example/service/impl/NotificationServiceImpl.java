package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.notification.NotificationDto;
import org.example.mapper.notification.CreateNotificationMapper;
import org.example.model.Notification;
import org.example.model.User;
import org.example.model.enums.NotificationType;
import org.example.repository.NotificationRepository;
import org.example.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final CreateNotificationMapper createNotificationMapper;


    @Override
    public NotificationDto findById(Integer id) {
            return createNotificationMapper.toDto(notificationRepository.findById(id).get());
    }


    @Override
    public void save(NotificationDto notification) {
        notificationRepository.save(createNotificationMapper.toNotification(notification));
    }

    @Override
    public List<Notification> getAllNotificationsByUserId(Integer userId) {
        return notificationRepository.findAllByUserId(userId);

    }

    @Override
    public void deleteById(Integer id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public void notifyUserUnblocked(User user) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUser(user);
        notificationDto.setTitle("Your profile blocked!");
        notificationDto.setMessage(NotificationType.PROFILE_BLOCKED_NOTIFICATION.format(user.getName(),user.getSurname()));
        notificationRepository.save(createNotificationMapper.toNotification(notificationDto));
    }

    @Override
    public void notifyUserBlocked(User user) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUser(user);
        notificationDto.setTitle("Your profile unblocked!");
        notificationDto.setMessage(NotificationType.PROFILE_UNBLOCKED_NOTIFICATION.format(user.getName(),user.getSurname()));
        notificationRepository.save(createNotificationMapper.toNotification(notificationDto));
    }

}
