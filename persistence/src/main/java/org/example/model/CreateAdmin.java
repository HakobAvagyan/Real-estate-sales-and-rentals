package org.example.model;

import lombok.RequiredArgsConstructor;
import org.example.model.enums.Gender;
import org.example.model.enums.Role;
import org.example.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class CreateAdmin implements CommandLineRunner {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String @NonNull ... args) {

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
                user.setBirthDate(LocalDate.parse("2026-05-01"));
                user.setGender(Gender.MALE);
                userRepository.save(user);
            }
        }
}
