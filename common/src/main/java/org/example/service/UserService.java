package org.example.service;


import org.example.dto.user.ChangePasswordRequest;
import org.example.dto.user.ResetPasswordRequest;
import org.example.dto.user.UserRegisterDto;
import org.example.dto.user.UserRequestDto;
import org.example.model.enums.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserRegisterDto findByEmail(String username);

    ChangePasswordRequest changePassword(String email, String oldPassword, String newPassword);

    ChangePasswordRequest changePasswordByEmail(String email);

    ResetPasswordRequest resetPassword(String email, String code, String newPassword, String newConfirmPassword);

    UserRegisterDto save(UserRegisterDto userRegisterDto, MultipartFile file);

    UserRegisterDto createManager(UserRegisterDto userRegisterDto, MultipartFile file);

    List<UserRequestDto> findAll();

    UserRegisterDto save(UserRegisterDto userRegisterDto);

    void deleteById(int id);

    void toggleUserBlockStatus(int id);

    UserRequestDto findById(int id);

    UserRegisterDto update(UserRegisterDto userRegisterDto, MultipartFile file);

    List<UserRequestDto> findAllByRoleIn(List<Role> roles);

    boolean verifyUser(String email, String verifyCode);

    boolean chekUserById(int id);

    void removeUserPicture(UserRequestDto userRequestDto);
}
