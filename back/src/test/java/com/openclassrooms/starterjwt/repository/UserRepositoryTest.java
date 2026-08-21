package com.openclassrooms.starterjwt.repository;

import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmailReturnsTheMatchingUser() {
        User user = User.builder()
                .email("user@example.com")
                .lastName("Doe")
                .firstName("Jane")
                .password("encoded-password")
                .build();
        userRepository.save(user);

        assertThat(userRepository.findByEmail("user@example.com"))
                .isPresent()
                .get()
                .extracting(User::getEmail)
                .isEqualTo("user@example.com");
    }
}
