package org.example.rest;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.CreateAdmin;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest(classes = {RestApplication.class, RestApplicationTests.TestConfig.class})
class RestApplicationTests {

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

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
