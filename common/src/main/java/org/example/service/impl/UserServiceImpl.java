package org.example.service.impl;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.notification.NotificationRequestDto;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
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
    public void changePassword(String email,
                               String oldPassword,
                               String newPassword,
                               String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            log.error("Passwords do not match");
            throw new BusinessException(ErrorCode.PASSWORDS_DO_NOT_MATCH, email);
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_BY_EMAIL, email));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.error("Passwords do not match");
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

        changePasswordRequestMapper.toChangePasswordRequestDto(savedUser);
    }

    @Override
    public void changePasswordByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_BY_EMAIL,
                        email));
        String verificationCode = generateVerificationCode();
        try {
            sendMailService.sendVerificationMailHtml(user.getEmail(), verificationCode);
            user.setVerificationCode(verificationCode);
            userRepository.save(user);
            changePasswordRequestMapper.toChangePasswordRequestDto(user);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}, and exception: {}", email, e.getMessage());
        }
    }

    @Override
    public void resetPassword(String email,
                              String code,
                              String newPassword,
                              String newConfirmPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_BY_EMAIL, email));

        if (!verifyUser(email, code)) {
            log.error("Password reset verification failed for email: {}", email);
            throw new BusinessException(ErrorCode.VERIFICATION_FAILED, email);
        }

        if (!newPassword.equals(newConfirmPassword)) {
            log.error("New passwords do not match with new confirm password");
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

        userResetPasswordMapper.toUserResetPasswordDto(savedUser);
    }

    @Override
    public UserRegisterDto save(UserRegisterDto userRegisterDto,
                                MultipartFile multipartFile) {
        User user = userRegisterMapper.toUser(userRegisterDto);
        editImage(multipartFile, user);
        if (userRegisterDto.getId() != 0) {
            userRepository.findById(userRegisterDto.getId()).ifPresent(
                    productOptional ->
                            userRegisterDto.setPicName(productOptional.getPicName()));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String verificationCode = generateVerificationCode();
        try {
            sendMailService.sendVerificationMailHtml(user.getEmail(), verificationCode);
            user.setVerificationCode(verificationCode);
        } catch (MessagingException e) {
            log.error("Failed to send verification code for register email: {}, and exception: {}", user.getEmail(), e.getMessage());
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
    public void createManager(UserRegisterDto userRegisterDto,
                              MultipartFile file) {
        userRegisterDto.setRole(Role.MANAGER);
        save(userRegisterDto, file);
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userResponseMapper.toUserResponseDtoList(userRepository.findAll().stream().filter(user -> user.getRole() != Role.ADMIN).toList());
    }

    @Override
    public void deleteById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));
        assertMayEditUserProfile(user.getId());
        userRepository.deleteById(user.getId());
        sendMailService.sendMail(
                user.getEmail(),
                "Your account has been deleted",
                NotificationType.PROFILE_REMOVED_NOTIFICATION.format(user.getName(), user.getSurname())
        );
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
            log.info("User {} is now blocked", user.getEmail());
            notificationService.notifyUserBlocked(user);
        } else {
            log.info("User {} is now unblocked", user.getEmail());
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
                                MultipartFile multipartFile,
                                 int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND, id));
        assertMayEditUserProfile(user.getId());
        user.setName(userUpdateDto.getName());
        user.setSurname(userUpdateDto.getSurname());
        user.setEmail(userUpdateDto.getEmail());
        user.setPhone(userUpdateDto.getPhone());
        user.setBirthDate(userUpdateDto.getBirthDate());
        user.setPassportDetails(userUpdateDto.getPassportDetails());
        editImage(multipartFile, user);
        User savedUser = userRepository.save(user);

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
    public List<UserResponseDto> findUserByRole(Role roles) {
        return userResponseMapper.toUserResponseDtoList(userRepository.findUserByRole(roles));
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
        log.error("Invalid verification code");
        return false;
    }

    @Override
    public boolean isRecentlyVerified(HttpSession session) {
        LocalDateTime verifiedAtObj = (LocalDateTime) session.getAttribute("passwordResetVerifiedAt");
        if (verifiedAtObj == null) {
            return true;
        }
        return !verifiedAtObj.isAfter(LocalDateTime.now().minusMinutes(5));
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
                if (!file.delete()) {
                    log.error("Failed to delete file {}", user.getPicName());
                    throw new RuntimeException("Failed to delete file: " + file.getName());
                }
            }
            user.setPicName(null);
            userRepository.save(user);
        }
    }

    @Override
    public Map<Integer, String> getSellerPhoneMap(List<Integer> userIds) {
        return userIds.stream()
                .collect(Collectors.toMap(
                        uid -> uid,
                        uid -> {
                            UserResponseDto u = findById(uid);
                            return u.getPhone() != null ? u.getPhone() : "";
                        }
                ));
    }

    private String generateVerificationCode() {
        int code = random.nextInt(1000, 9999);
        return String.valueOf(code);
    }

    private void assertMayEditUserProfile(int targetUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            log.error("Invalid authentication");
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }
        User current = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_BY_EMAIL, auth.getName()));
        if (current.getId() == targetUserId) {
            return;
        }
        if (current.getRole() == Role.ADMIN) {
            log.info("ADMIN-user profile");
            return;
        }
        throw new BusinessException(ErrorCode.PROFILE_EDIT_NOT_ALLOWED);
    }

    private void editImage(MultipartFile multipartFile, User user) {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" +
                    multipartFile.getOriginalFilename();
            File file = new File(imageDirectoryPath + fileName);
            try {
                multipartFile.transferTo(file);
                user.setPicName(fileName);
            } catch (IOException e) {
                log.error("Failed to save user image: {}", e.getMessage());
            }
        }
    }

}
