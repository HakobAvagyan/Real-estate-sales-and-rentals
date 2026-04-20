package org.example.service;


import jakarta.servlet.http.HttpSession;
import org.example.dto.user.UserRegisterDto;
import org.example.dto.user.UserResponseDto;
import org.example.dto.user.UserUpdateDto;
import org.example.model.enums.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserRegisterDto findByEmail(String username);

    void changePassword(String email, String oldPassword, String newPassword, String confirmPassword);

    void changePasswordByEmail(String email);

    void resetPassword(String email, String code, String newPassword, String newConfirmPassword);

    UserRegisterDto save(UserRegisterDto userRegisterDto, MultipartFile file);

    void createManager(UserRegisterDto userRegisterDto, MultipartFile file);

    List<UserResponseDto> findAll();

    void deleteById(int id);

    void toggleUserBlockStatus(int id);

    UserResponseDto findById(int id);

    UserUpdateDto update(UserUpdateDto userUpdateDto, MultipartFile file,int id);

    List<UserResponseDto> findUserByRole(Role roles);

    boolean verifyUser(String email, String verifyCode);

    boolean isRecentlyVerified(HttpSession session);

    void removeUserPicture(int userId);

}
