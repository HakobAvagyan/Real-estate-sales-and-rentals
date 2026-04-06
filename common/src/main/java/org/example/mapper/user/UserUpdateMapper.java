package org.example.mapper.user;

import org.example.dto.user.UserUpdateDto;
import org.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserUpdateMapper {

    UserUpdateDto toUserUpdateDto(User user);

    User toUser(UserUpdateDto userUpdateDto);

}
