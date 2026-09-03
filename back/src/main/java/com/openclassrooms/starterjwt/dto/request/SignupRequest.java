package com.openclassrooms.starterjwt.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class SignupRequest {
    @NotBlank @Size(max = 50) @Email
    private String email;
    @NotBlank @Size(min = 3, max = 20)
    private String firstName;
    @NotBlank @Size(min = 3, max = 20)
    private String lastName;
    @NotBlank @Size(min = 6, max = 40)
    private String password;
}