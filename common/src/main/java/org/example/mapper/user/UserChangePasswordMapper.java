package org.example.mapper.user;

import org.example.dto.user.UserChangePasswordDto;
import org.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserChangePasswordMapper {

    UserChangePasswordDto toUserChangePasswordDto(User user);

    User toUser(UserChangePasswordDto userChangePasswordDto);
}
