package org.example.service;


import org.example.dto.UserRegisterDto;
import org.example.dto.UserRequestDto;
import org.example.model.enums.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<UserRegisterDto> findByEmail(String username);

    UserRegisterDto save(UserRegisterDto userRegisterDto, MultipartFile file);

    List<UserRequestDto> findAll();

    UserRegisterDto save(UserRegisterDto userRegisterDto);

    void deleteById(int id);

    Optional<UserRegisterDto> findById(int id);

    UserRegisterDto update(UserRegisterDto userRegisterDto);

    List<UserRequestDto> findAllByRole(Role role);

}
