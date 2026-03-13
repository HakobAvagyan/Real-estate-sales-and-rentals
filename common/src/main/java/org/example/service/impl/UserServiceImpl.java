package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserRegisterDto;
import org.example.dto.user.UserRequestDto;
import org.example.mapper.user.UserRegisterMapper;
import org.example.mapper.user.UserRequestMapper;
import org.example.model.User;
import org.example.model.enums.Role;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${system.upload.images.directory.path}")
    private String imageDirectoryPath;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<UserRegisterDto> findByEmail(String username) {
        return userRepository.findByEmail(username).map(UserRegisterMapper::toUserRegisterDto);
    }

    @Override
    public Optional<UserRegisterDto> changePassword(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            User savedUser = userRepository.save(user);
            return Optional.of(UserRegisterMapper.toUserRegisterDto(savedUser));
        }
        return Optional.empty();

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
    public UserRegisterDto update(UserRegisterDto userRegisterDto) {
        User user = UserRegisterMapper.toUser(userRegisterDto);
        User savedUser = userRepository.save(user);
        return UserRegisterMapper.toUserRegisterDto(savedUser);
    }

    @Override
    public List<UserRequestDto> findAllByRoleIn(List<Role> roles) {
        return userRepository.findAllByRoleIn(roles).stream().map(UserRequestMapper::toUserRequestDto).toList();
    }


}
