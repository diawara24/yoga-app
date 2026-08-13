package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.TeacherDto;

import java.util.List;

public interface TeacherService {
    List<TeacherDto> findAll();

    TeacherDto findById(Long id);
}
