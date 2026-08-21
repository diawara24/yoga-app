package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findDtoByIdMapsTheFoundUser() {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .lastName("Doe")
                .firstName("Jane")
                .password("encoded-password")
                .build();
        UserDto expected = new UserDto(1L, "user@example.com", "Doe", "Jane", false,
                null, null, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expected);

        UserDto result = userService.findDtoById(1L);

        assertThat(result).isSameAs(expected);
        verify(userRepository).findById(1L);
        verify(userMapper).toDto(user);
    }
}
