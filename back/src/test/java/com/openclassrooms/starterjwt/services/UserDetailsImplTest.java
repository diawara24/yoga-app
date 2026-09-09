package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.services.impl.UserDetailsImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    @Test
    void builder_setsAllFields() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .admin(true)
                .password("secret")
                .build();

        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getUsername()).isEqualTo("user@example.com");
        assertThat(userDetails.getFirstName()).isEqualTo("Jane");
        assertThat(userDetails.getLastName()).isEqualTo("Doe");
        assertThat(userDetails.getAdmin()).isTrue();
        assertThat(userDetails.getPassword()).isEqualTo("secret");
    }

    @Test
    void getAuthorities_returnsEmptySet() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder().id(1L).build();

        assertThat(userDetails.getAuthorities()).isEmpty();
    }

    @Test
    void accountFlags_areAllTrue() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder().id(1L).build();

        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void equals_sameInstance_returnsTrue() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder().id(1L).build();

        assertThat(userDetails.equals(userDetails)).isTrue();
    }

    @Test
    void equals_null_returnsFalse() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder().id(1L).build();

        assertThat(userDetails.equals(null)).isFalse();
    }

    @Test
    void equals_differentClass_returnsFalse() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder().id(1L).build();

        assertThat(userDetails.equals("not a UserDetailsImpl")).isFalse();
    }

    @Test
    void equals_sameId_returnsTrue() {
        UserDetailsImpl a = UserDetailsImpl.builder().id(1L).username("a@a.com").build();
        UserDetailsImpl b = UserDetailsImpl.builder().id(1L).username("b@b.com").build();

        assertThat(a.equals(b)).isTrue();
    }

    @Test
    void equals_differentId_returnsFalse() {
        UserDetailsImpl a = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl b = UserDetailsImpl.builder().id(2L).build();

        assertThat(a.equals(b)).isFalse();
    }
}
