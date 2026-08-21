package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.impl.TeacherServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherMapper teacherMapper;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    @Test
    void findByIdReturnsMappedTeacher() {
        Teacher teacher = new Teacher(1L, "Doe", "Jane", null, null);
        TeacherDto expected = new TeacherDto(1L, "Doe", "Jane", null, null);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(teacherMapper.toDto(teacher)).thenReturn(expected);

        assertThat(teacherService.findById(1L)).isSameAs(expected);
    }

    @Test
    void findAllReturnsMappedTeachers() {
        Teacher teacher = new Teacher(1L, "Doe", "Jane", null, null);
        TeacherDto expected = new TeacherDto(1L, "Doe", "Jane", null, null);
        when(teacherRepository.findAll()).thenReturn(List.of(teacher));
        when(teacherMapper.toDto(teacher)).thenReturn(expected);

        assertThat(teacherService.findAll()).containsExactly(expected);
    }
}