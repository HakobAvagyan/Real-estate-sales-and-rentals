package org.example.mapper.user;

import org.example.dto.user.UserRequestDto;
import org.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRequestMapper {

    UserRequestDto toUserRequestDto(User user);

    User toUser(UserRequestDto userRequestDto);
}
