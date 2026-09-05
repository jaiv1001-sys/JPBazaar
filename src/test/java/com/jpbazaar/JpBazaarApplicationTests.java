package com.jpbazaar;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JpBazaarApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring Boot ApplicationContext initializes cleanly.
    }
}
