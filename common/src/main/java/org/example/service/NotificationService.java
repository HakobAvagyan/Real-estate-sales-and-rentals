package org.example.service;

import org.example.model.Notification;

import java.util.Optional;

public interface NotificationService {

    Optional<Notification> findById(Integer id);

    Optional<Notification> findByUserId(Integer userId);

    void save(Notification notification);

    void deleteById(Integer id);

    Notification update(Notification notification);

}
