package org.example.service;


import org.example.dto.user.ChangePasswordRequest;
import org.example.dto.user.ResetPasswordRequest;
import org.example.dto.user.UserRegisterDto;
import org.example.dto.user.UserResponseDto;
import org.example.dto.user.UserUpdateDto;
import org.example.model.enums.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserRegisterDto findByEmail(String username);

    ChangePasswordRequest changePassword(String email, String oldPassword, String newPassword, String confirmPassword);

    ChangePasswordRequest changePasswordByEmail(String email);

    ResetPasswordRequest resetPassword(String email, String code, String newPassword, String newConfirmPassword);

    UserRegisterDto save(UserRegisterDto userRegisterDto, MultipartFile file);

    UserRegisterDto createManager(UserRegisterDto userRegisterDto, MultipartFile file);

    List<UserResponseDto> findAll();

    void deleteById(int id);

    void toggleUserBlockStatus(int id);

    UserResponseDto findById(int id);

    UserUpdateDto update(UserUpdateDto userUpdateDto, MultipartFile file);

    List<UserResponseDto> findAllByRoleIn(List<Role> roles);

    boolean verifyUser(String email, String verifyCode);

    boolean existsById(int id);

    void removeUserPicture(int userId);

    int getIdByEmail(String email);
}
