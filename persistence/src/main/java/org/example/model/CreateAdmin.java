package org.example.model;

import lombok.RequiredArgsConstructor;
import org.example.model.enums.Role;
import org.example.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateAdmin implements CommandLineRunner {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) {

            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {

                User user = new User();
                user.setName("admin");
                user.setSurname("admin");
                user.setEmail("admin@gmail.com");
                user.setPhone("+374 98 123456");
                user.setPassword(passwordEncoder.encode("admin123"));
                user.setPassportDetails("AN1234567");
                user.setPicName("img_1.png");
                user.setRole(Role.ADMIN);

                userRepository.save(user);
            }
        }
}
