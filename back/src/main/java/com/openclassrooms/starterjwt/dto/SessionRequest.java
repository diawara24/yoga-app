package com.openclassrooms.starterjwt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class SessionRequest {

    @NotBlank(message = "{error.session.name.not-blank}")
    @Size(max = 50, message = "{error.session.name.size}")
    private String name;

    @NotNull(message = "{error.session.date.not-null}")
    private Date date;

    @NotNull(message = "{error.session.teacher-id.not-null}")
    private Long teacher_id;

    @NotNull(message = "{error.session.description.not-null}")
    @Size(max = 2500, message = "{error.session.description.size}")
    private String description;
}