package org.example.mapper.notification;

import org.example.dto.notification.NotificationRequestDto;
import org.example.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationRequestMapper {

    Notification toNotification(NotificationRequestDto dto);

    NotificationRequestDto toDto(Notification notification);
}
