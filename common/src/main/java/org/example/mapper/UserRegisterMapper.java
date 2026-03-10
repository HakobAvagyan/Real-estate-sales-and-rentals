package org.example.mapper;

import org.example.dto.UserRegisterDto;
import org.example.model.User;

public class UserRegisterMapper {

    public static UserRegisterDto toUserRegisterDto(User user) {
        if (user == null) {
            return null;
        }
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setId(user.getId());
        userRegisterDto.setName(user.getName());
        userRegisterDto.setSurname(user.getSurname());
        userRegisterDto.setEmail(user.getEmail());
        userRegisterDto.setPhone(user.getPhone());
        userRegisterDto.setPassword(user.getPassword());
        userRegisterDto.setPassportDetails(user.getPassportDetails());
        userRegisterDto.setPicName(user.getPicName());
        userRegisterDto.setRole(user.getRole());
        userRegisterDto.setBlocked(user.isBlocked());
        userRegisterDto.setCreatedAt(user.getCreatedAt());
        return userRegisterDto;
    }

    public static User toUser(UserRegisterDto userRegisterDto) {
        if (userRegisterDto == null) {
            return null;
        }
        User user = new User();
        user.setId(userRegisterDto.getId());
        user.setName(userRegisterDto.getName());
        user.setSurname(userRegisterDto.getSurname());
        user.setEmail(userRegisterDto.getEmail());
        user.setPhone(userRegisterDto.getPhone());
        user.setPassword(userRegisterDto.getPassword());
        user.setPassportDetails(userRegisterDto.getPassportDetails());
        user.setPicName(userRegisterDto.getPicName());
        user.setRole(userRegisterDto.getRole());
        user.setBlocked(userRegisterDto.isBlocked());
        user.setCreatedAt(userRegisterDto.getCreatedAt());
        return user;
    }

}
