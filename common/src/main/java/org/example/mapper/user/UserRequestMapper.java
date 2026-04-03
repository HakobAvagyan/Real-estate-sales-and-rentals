package org.example.mapper.user;

import org.example.dto.user.UserRequestDto;
import org.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRequestMapper {

    UserRequestDto toUserRequestDto(User user);

    User toUser(UserRequestDto userRequestDto);

    List<UserRequestDto> toUserRequestDtoList(List<User> users);

    List<User> toUserList(List<UserRequestDto> userRequestDto);

}
