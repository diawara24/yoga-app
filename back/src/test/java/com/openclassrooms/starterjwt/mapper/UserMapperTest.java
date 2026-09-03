package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDto_mapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .password("encoded-password")
                .admin(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        UserDto dto = userMapper.toDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEmail()).isEqualTo("user@example.com");
        assertThat(dto.getFirstName()).isEqualTo("Jane");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getPassword()).isEqualTo("encoded-password");
        assertThat(dto.isAdmin()).isTrue();
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toEntity_mapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        UserDto dto = new UserDto(1L, "user@example.com", "Doe", "Jane", true,
                "encoded-password", now, now);

        User user = userMapper.toEntity(dto);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("user@example.com");
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getPassword()).isEqualTo("encoded-password");
        assertThat(user.isAdmin()).isTrue();
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toDto_null_returnsNull() {
        assertThat(userMapper.toDto((User) null)).isNull();
    }

    @Test
    void toEntity_null_returnsNull() {
        assertThat(userMapper.toEntity((UserDto) null)).isNull();
    }

    @Test
    void toDto_list_mapsAllElements() {
        User user = User.builder().id(1L).email("a@a.com").firstName("Jane").lastName("Doe")
                .password("pwd").admin(false).build();

        List<UserDto> dtos = userMapper.toDto(List.of(user));

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getEmail()).isEqualTo("a@a.com");
    }

    @Test
    void toDto_list_null_returnsNull() {
        assertThat(userMapper.toDto((List<User>) null)).isNull();
    }

    @Test
    void toEntity_list_mapsAllElements() {
        UserDto dto = new UserDto(1L, "a@a.com", "Doe", "Jane", false, "pwd", null, null);

        List<User> users = userMapper.toEntity(List.of(dto));

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("a@a.com");
    }

    @Test
    void toEntity_list_null_returnsNull() {
        assertThat(userMapper.toEntity((List<UserDto>) null)).isNull();
    }
}