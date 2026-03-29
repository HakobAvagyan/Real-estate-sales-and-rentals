package org.example.mapper.notification;

import org.example.dto.notification.NotificationDto;
import org.example.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreateNotificationMapper {

    Notification toNotification(NotificationDto dto);

    NotificationDto toDto(Notification notification);

}
