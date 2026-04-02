package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.notification.NotificationRequestDto;
import org.example.dto.notification.NotificationResponseDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.mapper.notification.NotificationRequestMapper;
import org.example.mapper.notification.NotificationResponseMapper;
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
    private final NotificationResponseMapper notificationResponseMapper;
    private final NotificationRequestMapper notificationRequestMapper;


    @Override
    public NotificationRequestDto findById(Integer id, Integer userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND,id));

        if(notification.getUser().getId() != userId){
            throw new BusinessException(ErrorCode.TRY_AGAIN);
        }

        return notificationRequestMapper.toDto(notification);
    }


    @Override
    public void save(NotificationResponseDto notification) {
        notificationRepository.save(notificationResponseMapper.toNotification(notification));
    }

    @Override
    public List<NotificationRequestDto> getAllNotificationsByUserId(Integer userId) {
        return notificationRepository.findAllByUserId(userId).stream()
                .map(notificationRequestMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Integer id, Integer userId) {
        if(notificationRepository.findById(id).get().getUser().getId() != userId){
            throw new BusinessException(ErrorCode.TRY_AGAIN);
        }
        notificationRepository.deleteById(id);
    }

    @Override
    public void notifyUserBlocked(User user) {
        NotificationResponseDto notificationResponseDto = new NotificationResponseDto();
        notificationResponseDto.setUserId(user.getId());
        notificationResponseDto.setTitle("Your profile blocked!");
        notificationResponseDto.setMessage(NotificationType.PROFILE_BLOCKED_NOTIFICATION.format(user.getName(),user.getSurname()));
        notificationRepository.save(notificationResponseMapper.toNotification(notificationResponseDto));
    }

    @Override
    public void notifyUserUnblocked(User user) {
        NotificationResponseDto notificationResponseDto = new NotificationResponseDto();
        notificationResponseDto.setUserId(user.getId());
        notificationResponseDto.setTitle("Your profile unblocked!");
        notificationResponseDto.setMessage(NotificationType.PROFILE_UNBLOCKED_NOTIFICATION.format(user.getName(),user.getSurname()));
        notificationRepository.save(notificationResponseMapper.toNotification(notificationResponseDto));
    }

}
