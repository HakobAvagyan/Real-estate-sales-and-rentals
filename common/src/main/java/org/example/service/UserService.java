package org.example.service;


import org.example.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String username);

    User save(User user, MultipartFile file);

    List<User> findAll();

    void save(User user);

    void deleteById(int id);

    Optional<User> findById(int id);

    User update(User user);

}
