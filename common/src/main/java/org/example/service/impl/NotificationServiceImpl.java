package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.example.repository.UserRepository;
import org.example.service.NotificationService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationResponseMapper notificationResponseMapper;
    private final NotificationRequestMapper notificationRequestMapper;
    private final UserRepository userRepository;


    @Override
    public NotificationRequestDto findById(Integer id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND,id));
        if(notification.getUser().getId() != getCurrentUserId()){
            log.error("Unauthorized access attempt to notification id: {} by user id: {}", id, getCurrentUserId());
            throw new BusinessException(ErrorCode.TRY_AGAIN);
        }
        return notificationRequestMapper.toDto(notification);
    }


    @Override
    public void save(NotificationRequestDto notification) {
        notificationRepository.save(notificationRequestMapper.toNotification(notification));
    }

    @Override
    public List<NotificationResponseDto> getAllNotificationsByUserId() {
        return notificationRepository.findAllByUserId(getCurrentUserId()).stream()
                .map(notificationResponseMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Integer id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND, id));
        if(notification.getUser().getId() != getCurrentUserId()){
            log.error("Unauthorized delete attempt to notification id: {} by user id: {}", id, getCurrentUserId());
            throw new BusinessException(ErrorCode.TRY_AGAIN);
        }
        notificationRepository.deleteById(notification.getId());
    }

    @Override
    public void notifyUserBlocked(User user) {
        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        notificationRequestDto.setUserId(user.getId());
        notificationRequestDto.setTitle("Your profile blocked!");
        notificationRequestDto.setMessage(NotificationType.PROFILE_BLOCKED_NOTIFICATION.format(user.getName(),user.getSurname()));
        notificationRepository.save(notificationRequestMapper.toNotification(notificationRequestDto));
    }

    @Override
    public void notifyUserUnblocked(User user) {
        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        notificationRequestDto.setUserId(user.getId());
        notificationRequestDto.setTitle("Your profile unblocked!");
        notificationRequestDto.setMessage(NotificationType.PROFILE_UNBLOCKED_NOTIFICATION.format(user.getName(),user.getSurname()));
        notificationRepository.save(notificationRequestMapper.toNotification(notificationRequestDto));
    }


    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            log.error("Unauthenticated access attempt to get current user id");
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }

        String email = auth.getName();
        User byEmail = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_BY_EMAIL,email));
        return byEmail.getId();
    }
}
