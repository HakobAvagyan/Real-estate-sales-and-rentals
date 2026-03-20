package org.example.service;


import org.example.dto.user.UserRegisterDto;
import org.example.dto.user.UserRequestDto;
import org.example.dto.UserRegisterDto;
import org.example.dto.UserRequestDto;
import org.example.model.User;
import org.example.model.enums.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<UserRegisterDto> findByEmail(String username);

    Optional<UserRegisterDto> changePassword(String email, String password);

    UserRegisterDto save(UserRegisterDto userRegisterDto, MultipartFile file);

    List<UserRequestDto> findAll();

    UserRegisterDto save(UserRegisterDto userRegisterDto);

    void deleteById(int id);

    Optional<UserRegisterDto> findById(int id);

    UserRegisterDto update(UserRegisterDto userRegisterDto);

    List<UserRequestDto> findAllByRoleIn(List<Role> roles);

    boolean verifyUser(String email, String verifyCode);

    Optional<User> findByUsername(String username);
}
