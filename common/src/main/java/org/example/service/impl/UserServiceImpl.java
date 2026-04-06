package org.example.service.impl;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.dto.notification.NotificationRequestDto;
import org.example.dto.user.ChangePasswordRequest;
import org.example.dto.user.ResetPasswordRequest;
import org.example.dto.user.UserRegisterDto;
import org.example.dto.user.UserResponseDto;
import org.example.dto.user.UserUpdateDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.mapper.user.ChangePasswordRequestMapper;
import org.example.mapper.user.UserRegisterMapper;
import org.example.mapper.user.UserResetPasswordMapper;
import org.example.mapper.user.UserResponseMapper;
import org.example.mapper.user.UserUpdateMapper;
import org.example.model.User;
import org.example.model.enums.NotificationType;
import org.example.model.enums.Role;
import org.example.repository.UserRepository;
import org.example.service.NotificationService;
import org.example.service.SendMailService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${system.upload.images.directory.path}")
    private String imageDirectoryPath;

    private final SendMailService sendMailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRegisterMapper userRegisterMapper;
    private final UserResponseMapper userResponseMapper;
    private final UserResetPasswordMapper userResetPasswordMapper;
    private final ChangePasswordRequestMapper changePasswordRequestMapper;
    private final NotificationService notificationService;
    private final UserUpdateMapper userUpdateMapper;
    private final Random random = new Random();

    @Override
    public UserRegisterDto findByEmail(String username) {
        return userRepository.findByEmail(username)
                .map(userRegisterMapper::toUserRegisterDto)
                .orElse(null);
    }

    @Override
    public ChangePasswordRequest changePassword(String email,
                                                String oldPassword,
                                                String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_BY_EMAIL, email));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.OLD_PASSWORD_IS_INCORRECT);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        User savedUser = userRepository.save(user);

        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        notificationRequestDto.setUserId(savedUser.getId());
        notificationRequestDto.setTitle("You changed your password successfully!");
        notificationRequestDto.setMessage(
                NotificationType.PROFILE_PASSWORD_CHANGED_NOTIFICATION
                        .format(savedUser.getName(), savedUser.getSurname()));
        notificationService.save(notificationRequestDto);

        return changePasswordRequestMapper.toChangePasswordRequestDto(savedUser);
    }

    @Override
    public ChangePasswordRequest changePasswordByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_BY_EMAIL,
                        email));
        String verificationCode = generateVerificationCode();
        try {
            sendMailService.sendVerificationMailHtml(user.getEmail(), verificationCode);
            user.setVerificationCode(verificationCode);
            userRepository.save(user);
            return changePasswordRequestMapper.toChangePasswordRequestDto(user);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.TRY_AGAIN);
        }
    }

    @Override
    public ResetPasswordRequest resetPassword(String email,
                                              String code,
                                              String newPassword,
                                              String newConfirmPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_BY_EMAIL, email));

        if (!verifyUser(email, code)) {
            throw new BusinessException(ErrorCode.VERIFICATION_FAILED, email);
        }

        if (!newPassword.equals(newConfirmPassword)) {
            throw new BusinessException(ErrorCode.PASSWORDS_DO_NOT_MATCH, email);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        User savedUser = userRepository.save(user);

        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        notificationRequestDto.setUserId(savedUser.getId());
        notificationRequestDto.setTitle("You reset your password successfully!");
        notificationRequestDto.setMessage(NotificationType
                .PROFILE_PASSWORD_RESET_NOTIFICATION
                .format(savedUser.getName(), savedUser.getSurname()));
        notificationService.save(notificationRequestDto);

        return userResetPasswordMapper.toUserResetPasswordDto(savedUser);
    }

    @Override
    public UserRegisterDto save(UserRegisterDto userRegisterDto,
                                MultipartFile multipartFile) {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" +
                    multipartFile.getOriginalFilename();
            File file = new File(imageDirectoryPath + fileName);
            try {
                multipartFile.transferTo(file);
                userRegisterDto.setPicName(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (userRegisterDto.getId() != 0) {
            userRepository.findById(userRegisterDto.getId()).ifPresent(
                    productOptional ->
                            userRegisterDto.setPicName(productOptional.getPicName()));
        }
        User user = userRegisterMapper.toUser(userRegisterDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String verificationCode = generateVerificationCode();
        try {
            sendMailService.sendVerificationMailHtml(user.getEmail(), verificationCode);
            user.setVerificationCode(verificationCode);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        User savedUser = userRepository.save(user);

        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        notificationRequestDto.setUserId(savedUser.getId());
        notificationRequestDto.setTitle("You registered successfully!");
        notificationRequestDto.setMessage(NotificationType.USER_REGISTERED_NOTIFICATION
                .format(savedUser.getName(), savedUser.getSurname()));
        notificationService.save(notificationRequestDto);

        return userRegisterMapper.toUserRegisterDto(savedUser);
    }

    @Override
    public UserRegisterDto createManager(UserRegisterDto userRegisterDto,
                                         MultipartFile file) {
        userRegisterDto.setRole(Role.MANAGER);
        return save(userRegisterDto, file);
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userResponseMapper.toUserResponseDtoList(userRepository.findAll().stream().filter(user -> user.getRole() != Role.ADMIN).toList());
    }

    @Override
    public void deleteById(int id) {
        userRepository.findById(id).ifPresent(user -> {
            if (!existsById(id)) {
                throw new BusinessException(ErrorCode.CANNOT_DELETE_OWN_ACCOUNT);
            }
            userRepository.deleteById(user.getId());
        });
    }

    @Override
    @Transactional
    public void toggleUserBlockStatus(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));

        boolean isNowBlocked = !user.isBlocked();
        user.setBlocked(isNowBlocked);

        userRepository.save(user);

        if (isNowBlocked) {
            notificationService.notifyUserBlocked(user);
        } else {
            notificationService.notifyUserUnblocked(user);
        }
    }

    @Override
    public UserResponseDto findById(int id) {
        return userResponseMapper.toUserResponseDto(userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id)));
    }

    @Override
    public UserUpdateDto update( UserUpdateDto userUpdateDto,
                                MultipartFile multipartFile) {
        User user = userRepository.findById(userUpdateDto.getId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND, userUpdateDto.getId()));
        assertMayEditUserProfile(user.getId());
        UserUpdateDto userDto = userUpdateMapper.toUserUpdateDto(user);
        userDto.setName(userUpdateDto.getName());
        userDto.setSurname(userUpdateDto.getSurname());
        userDto.setEmail(userUpdateDto.getEmail());
        userDto.setPhone(userUpdateDto.getPhone());
        userDto.setPassportDetails(userUpdateDto.getPassportDetails());
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" +
                    multipartFile.getOriginalFilename();
            File file = new File(imageDirectoryPath + fileName);
            try {
                multipartFile.transferTo(file);
                userDto.setPicName(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        User savedUser = userRepository.save(userUpdateMapper.toUser(userDto));

        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        notificationRequestDto.setUserId(savedUser.getId());
        notificationRequestDto.setTitle("You update your profile successfully!");
        notificationRequestDto.setMessage(NotificationType.
                PROFILE_UPDATE_NOTIFICATION.
                format(savedUser.getName(), savedUser.getSurname()));
        notificationService.save(notificationRequestDto);

        return userUpdateMapper.toUserUpdateDto(savedUser);
    }

    @Override
    public List<UserResponseDto> findAllByRoleIn(List<Role> roles) {
        return userResponseMapper.toUserResponseDtoList(userRepository.findAllByRoleIn(roles));
    }

    @Override
    public boolean verifyUser(String email, String verifyCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, email));
        if (user.getVerificationCode() != null && user.getVerificationCode().equals(verifyCode)) {
            user.setVerificationCode(null);
            user.setBlocked(false);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_BY_EMAIL, email));
        if (user.getId() != id) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, id);
        }
        return true;
    }

    /**
     * Own profile always; ADMIN may edit any user (MVC /update form).
     */
    private void assertMayEditUserProfile(int targetUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }
        User current = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_BY_EMAIL, auth.getName()));
        if (current.getId() == targetUserId) {
            return;
        }
        if (current.getRole() == Role.ADMIN) {
            return;
        }
        throw new BusinessException(ErrorCode.PROFILE_EDIT_NOT_ALLOWED);
    }

    @Override
    public void removeUserPicture(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND, userId));
        assertMayEditUserProfile(userId);
        if (user.getPicName() != null) {
            File file = new File(imageDirectoryPath + user.getPicName());
            if (file.exists()) {
                file.delete();
            }
            user.setPicName(null);
            userRepository.save(user);
        }
    }

    private String generateVerificationCode() {
        int code = random.nextInt(1000, 9999);
        return String.valueOf(code);
    }

}
