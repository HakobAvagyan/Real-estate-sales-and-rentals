package org.example.mapper.user;

import org.example.dto.user.UserChangePasswordDto;
import org.example.model.User;

public class UserChangePasswordMapper {

    public static UserChangePasswordDto toUserChangePasswordDto(User user) {
        if (user == null) {
            return null;
        }
        UserChangePasswordDto result = new UserChangePasswordDto();
        result.setEmail(user.getEmail());
        result.setPassword(user.getPassword());
        return result;
    }

    public static User toUser(UserChangePasswordDto userChangePasswordDto) {
        if (userChangePasswordDto == null) {
            return null;
        }
        User newUser = new User();
        newUser.setEmail(userChangePasswordDto.getEmail());
        newUser.setPassword(userChangePasswordDto.getPassword());
        return newUser;
    }

}
