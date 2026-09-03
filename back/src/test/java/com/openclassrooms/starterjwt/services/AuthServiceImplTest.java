package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.request.LoginRequest;
import com.openclassrooms.starterjwt.dto.request.SignupRequest;
import com.openclassrooms.starterjwt.dto.response.JwtResponse;
import com.openclassrooms.starterjwt.dto.response.MessageResponse;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.impl.AuthServiceImpl;
import com.openclassrooms.starterjwt.services.impl.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void authenticate_validCredentials_returnsJwtResponse() {
        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(1L).username("user@example.com").firstName("A").lastName("B")
                .admin(true).password("encoded").build();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(principal)).thenReturn("fake-jwt");
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(User.builder().email("user@example.com")
                        .firstName("A").lastName("B").password("x").admin(true).build()));

        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("secret");

        JwtResponse response = authService.authenticate(request);

        assertThat(response.getToken()).isEqualTo("fake-jwt");
        assertThat(response.getAdmin()).isTrue();
    }

    @Test
    void register_newEmail_savesUserAndReturnsMessage() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret1234")).thenReturn("encoded-pwd");

        SignupRequest request = new SignupRequest();
        request.setEmail("new@example.com");
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPassword("secret1234");

        MessageResponse response = authService.register(request);

        assertThat(response.message()).isEqualTo("User registered successfully!");
        verify(userRepository).save(argThat(u -> u.getPassword().equals("encoded-pwd")));
    }

    @Test
    void register_emailAlreadyTaken_throwsBadRequestException() {
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        SignupRequest request = new SignupRequest();
        request.setEmail("taken@example.com");
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPassword("secret1234");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class);

        verify(userRepository, never()).save(any());
    }
}