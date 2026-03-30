package org.example.mapper.notification;

import org.example.dto.notification.NotificationResponseDto;
import org.example.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationResponseMapper {

    Notification toNotification(NotificationResponseDto dto);

    NotificationResponseDto toDto(Notification notification);

}
