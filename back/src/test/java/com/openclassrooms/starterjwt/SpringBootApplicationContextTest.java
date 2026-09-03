package com.openclassrooms.starterjwt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = SpringBootSecurityJwtApplication.class)
@ActiveProfiles("test")
class SpringBootApplicationContextTest {

    @Test
    void contextLoads() {
    }
}
