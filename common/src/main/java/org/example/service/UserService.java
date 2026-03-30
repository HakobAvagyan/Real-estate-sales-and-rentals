package org.example.service;


import org.example.dto.user.ChangePasswordRequest;
import org.example.dto.user.ResetPasswordRequest;
import org.example.dto.user.UserRegisterDto;
import org.example.dto.user.UserRequestDto;
import org.example.model.enums.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<UserRegisterDto> findByEmail(String username);

    Optional<ChangePasswordRequest> changePassword(String email, String oldPassword, String newPassword);

    Optional<ChangePasswordRequest> changePasswordByEmail(String email);

    Optional<ResetPasswordRequest> resetPassword(String email, String code, String newPassword, String newConfirmPassword);

    UserRegisterDto save(UserRegisterDto userRegisterDto, MultipartFile file);

    List<UserRequestDto> findAll();

    UserRegisterDto save(UserRegisterDto userRegisterDto);

    void deleteById(int id);

    void toggleUserBlockStatus(int id);

    Optional<UserRegisterDto> findById(int id);

    UserRegisterDto update(UserRegisterDto userRegisterDto, MultipartFile file);

    List<UserRequestDto> findAllByRoleIn(List<Role> roles);

    boolean verifyUser(String email, String verifyCode);

}
