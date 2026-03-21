package org.example.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserChangePasswordDto;
import org.example.dto.user.UserRegisterDto;
import org.example.dto.user.UserRequestDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.mapper.user.UserChangePasswordMapper;
import org.example.mapper.user.UserRegisterMapper;
import org.example.mapper.user.UserRequestMapper;
import org.example.model.User;
import org.example.model.enums.Role;
import org.example.repository.UserRepository;
import org.example.service.SendMailService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${system.upload.images.directory.path}")
    private String imageDirectoryPath;

    private final SendMailService sendMailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    @Override
    public Optional<UserRegisterDto> findByEmail(String username) {
        return userRepository.findByEmail(username).map(UserRegisterMapper::toUserRegisterDto);
    }

    @Override
    public Optional<UserChangePasswordDto> changePassword(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_BY_EMAIL, email));
        user.setPassword(passwordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        return Optional.of(UserChangePasswordMapper.toUserChangePasswordDto(savedUser));
    }

    @Override
    public Optional<UserChangePasswordDto> changePasswordByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_BY_EMAIL, email));
        String verificationCode = generateVerificationCode();
        try {
            sendMailService.sendVerificationMailHtml(user.getEmail(),verificationCode);
            user.setVerificationCode(verificationCode);
            userRepository.save(user);
            return Optional.of(UserChangePasswordMapper.toUserChangePasswordDto(user));
        } catch (MessagingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean checkOldPassword(String oldPassword, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_BY_EMAIL, email));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.OLD_PASSWORD_IS_INCORRECT);
        }
        return true;
    }


    @Override
    public UserRegisterDto save(UserRegisterDto userRegisterDto, MultipartFile multipartFile) {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageDirectoryPath + fileName);
            try {
                multipartFile.transferTo(file);
                userRegisterDto.setPicName(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            userRepository.findById(userRegisterDto.getId())
                    .ifPresent(productOptional -> userRegisterDto.setPicName(productOptional.getPicName()));
        }
        User user = UserRegisterMapper.toUser(userRegisterDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String verificationCode = generateVerificationCode();
        try {
            sendMailService.sendVerificationMailHtml(user.getEmail(),verificationCode);
            user.setVerificationCode(verificationCode);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        User savedUser = userRepository.save(user);
        return UserRegisterMapper.toUserRegisterDto(savedUser);
    }


    @Override
    public List<UserRequestDto> findAll() {
        return userRepository.findAll().stream().map(UserRequestMapper::toUserRequestDto).toList();
    }


    @Override
    public UserRegisterDto save(UserRegisterDto userRegisterDto) {
        User user = UserRegisterMapper.toUser(userRegisterDto);
        User savedUser = userRepository.save(user);
        return UserRegisterMapper.toUserRegisterDto(savedUser);
    }

    @Override
    public void deleteById(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<UserRegisterDto> findById(int id) {
        return userRepository.findById(id).map(UserRegisterMapper::toUserRegisterDto);
    }


    @Override
    public UserRegisterDto update(UserRegisterDto userRegisterDto, MultipartFile multipartFile) {
        User existingUser = userRepository.findById(userRegisterDto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userRegisterDto.getId()));

        existingUser.setName(userRegisterDto.getName());
        existingUser.setSurname(userRegisterDto.getSurname());
        existingUser.setEmail(userRegisterDto.getEmail());
        existingUser.setPhone(userRegisterDto.getPhone());
        existingUser.setPassportDetails(userRegisterDto.getPassportDetails());
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageDirectoryPath + fileName);
            try {
                multipartFile.transferTo(file);
                existingUser.setPicName(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        User savedUser = userRepository.save(existingUser);
        return UserRegisterMapper.toUserRegisterDto(savedUser);
    }

    @Override
    public List<UserRequestDto> findAllByRoleIn(List<Role> roles) {
        return userRepository.findAllByRoleIn(roles).stream().map(UserRequestMapper::toUserRequestDto).toList();
    }

    @Override
    public boolean verifyUser(String email, String verifyCode) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getVerificationCode() != null
                && user.get().getVerificationCode().equals(verifyCode)) {
            user.get().setVerificationCode(null);
            user.get().setBlocked(false);
            userRepository.save(user.get());
            return true;
        }
        return false;
    }

    private String generateVerificationCode() {
        int code = random.nextInt(1000, 9999);
        return String.valueOf(code);
    }


}
