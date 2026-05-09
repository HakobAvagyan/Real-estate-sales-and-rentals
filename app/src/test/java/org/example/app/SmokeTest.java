package org.example.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class SmokeTest {

    @Autowired
    private org.example.service.PropertyService propertyService;

    @Autowired
    private org.example.service.UserService userService;

    @Test
    void contextLoadsAndServicesAreAvailable() {
        assertNotNull(propertyService);
        assertNotNull(userService);
    }
}
