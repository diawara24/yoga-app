package com.openclassrooms.starterjwt.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "{error.user.email.not-blank}")
    @Size(max = 50, message = "{error.user.email.size}")
    @Email(message = "{error.user.email.invalid}")
    private String email;

    @NotBlank(message = "{error.user.last-name.not-blank}")
    @Size(max = 20, message = "{error.user.last-name.size}")
    private String lastName;

    @NotBlank(message = "{error.user.first-name.not-blank}")
    @Size(max = 20, message = "{error.user.first-name.size}")
    private String firstName;

    private boolean admin;

    @JsonIgnore
    @Size(max = 120, message = "{error.user.password.size}")
    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
