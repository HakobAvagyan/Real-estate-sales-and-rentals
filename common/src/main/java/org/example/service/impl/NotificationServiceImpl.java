package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.Notification;
import org.example.repository.NotificationRepository;
import org.example.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;


    @Override
    public Optional<Notification> findById(Integer id) {
        return notificationRepository.findById(id);
    }

    @Override
    public Optional<Notification> findByUserId(Integer userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    @Override
    public void deleteById(Integer id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public Notification update(Notification notification) {
        return notificationRepository.save(notification);
    }
}
