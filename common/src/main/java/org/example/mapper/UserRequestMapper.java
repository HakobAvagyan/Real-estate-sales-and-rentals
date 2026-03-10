package org.example.mapper;

import org.example.dto.UserRequestDto;
import org.example.model.User;

public class UserRequestMapper {

    public static UserRequestDto toUserRequestDto(User user) {
        if (user == null) {
            return null;
        }
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setId(user.getId());
        userRequestDto.setName(user.getName());
        userRequestDto.setSurname(user.getSurname());
        userRequestDto.setEmail(user.getEmail());
        userRequestDto.setPhone(user.getPhone());
        userRequestDto.setPassportDetails(user.getPassportDetails());
        userRequestDto.setPicName(user.getPicName());
        userRequestDto.setRole(user.getRole());
        userRequestDto.setCreatedAt(user.getCreatedAt());

        return userRequestDto;
    }

    public static User toUser(UserRequestDto userRequestDto) {
        if (userRequestDto == null) {
            return null;
        }
        User user = new User();
        user.setId(userRequestDto.getId());
        user.setName(userRequestDto.getName());
        user.setSurname(userRequestDto.getSurname());
        user.setEmail(userRequestDto.getEmail());
        user.setPhone(userRequestDto.getPhone());
        user.setPassportDetails(userRequestDto.getPassportDetails());
        user.setPicName(userRequestDto.getPicName());
        user.setRole(userRequestDto.getRole());
        user.setCreatedAt(userRequestDto.getCreatedAt());

        return user;
    }
}
