package org.example.mapper.user;

import org.example.dto.user.UserResponseDto;
import org.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserResponseMapper {

    UserResponseDto toUserResponseDto(User user);

    User toUser(UserResponseDto userResponseDto);

    List<UserResponseDto> toUserResponseDtoList(List<User> users);

    List<User> toUserList(List<UserResponseDto> userResponseDto);

}
