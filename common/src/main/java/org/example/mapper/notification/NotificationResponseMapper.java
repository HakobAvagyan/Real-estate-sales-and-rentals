package org.example.mapper.notification;

import org.example.dto.notification.NotificationResponseDto;
import org.example.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationResponseMapper {

    @Mapping(source = "userId", target = "user.id")
    Notification toNotification(NotificationResponseDto dto);

    @Mapping(source = "user.id", target = "userId")
    NotificationResponseDto toDto(Notification notification);

}
