package org.example.mapper.user;

import org.example.dto.user.ResetPasswordRequest;
import org.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserResetPasswordMapper {

    ResetPasswordRequest toUserResetPasswordDto(User user);

    User toUser(ResetPasswordRequest resetPasswordRequest);
}
