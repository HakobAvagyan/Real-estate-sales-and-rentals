package org.example.app;

import org.junit.jupiter.api.Test;
import org.example.model.CreateAdmin;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
class AppApplicationTests {
    @Test
    void contextLoads() {
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        JavaMailSender javaMailSender() {
            return Mockito.mock(JavaMailSender.class);
        }

        @Bean("createAdmin")
        CreateAdmin createAdmin() {
            return Mockito.mock(CreateAdmin.class);
        }
    }

}
