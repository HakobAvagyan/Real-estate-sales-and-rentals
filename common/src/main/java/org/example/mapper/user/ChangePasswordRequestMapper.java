package org.example.mapper.user;

import org.example.dto.user.ChangePasswordRequest;
import org.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChangePasswordRequestMapper {

    ChangePasswordRequest toChangePasswordRequestDto(User user);

    User toUser(ChangePasswordRequest changePasswordRequest);
}
