package org.example.mapper.user;

import org.example.dto.user.UserRegisterDto;
import org.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRegisterMapper {

    UserRegisterDto toUserRegisterDto(User user);

    User toUser(UserRegisterDto userRegisterDto);
}
