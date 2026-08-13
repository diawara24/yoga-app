package com.openclassrooms.starterjwt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto {
    private Long id;

    @NotBlank(message = "{error.teacher.last-name.not-blank}")
    @Size(max = 20, message = "{error.teacher.last-name.size}")
    private String lastName;

    @NotBlank(message = "{error.teacher.first-name.not-blank}")
    @Size(max = 20, message = "{error.teacher.first-name.size}")
    private String firstName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
