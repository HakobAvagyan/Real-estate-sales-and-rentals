package org.example.mapper.notification;

import org.example.dto.notification.NotificationRequestDto;
import org.example.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationRequestMapper {

    @Mapping(source = "userId", target = "user.id")
    Notification toNotification(NotificationRequestDto dto);

    @Mapping(source = "user.id", target = "userId")
    NotificationRequestDto toDto(Notification notification);
}
