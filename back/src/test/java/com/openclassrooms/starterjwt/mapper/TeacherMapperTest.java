package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class TeacherMapperTest {

    private final TeacherMapper teacherMapper = Mappers.getMapper(TeacherMapper.class);

    @Test
    void toDto_mapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .createdAt(now)
                .updatedAt(now)
                .build();

        TeacherDto dto = teacherMapper.toDto(teacher);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("Jane");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toEntity_mapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        TeacherDto dto = new TeacherDto(1L, "Doe", "Jane", now, now);

        Teacher teacher = teacherMapper.toEntity(dto);

        assertThat(teacher.getId()).isEqualTo(1L);
        assertThat(teacher.getFirstName()).isEqualTo("Jane");
        assertThat(teacher.getLastName()).isEqualTo("Doe");
        assertThat(teacher.getCreatedAt()).isEqualTo(now);
        assertThat(teacher.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toDto_null_returnsNull() {
        assertThat(teacherMapper.toDto((Teacher) null)).isNull();
    }

    @Test
    void toEntity_null_returnsNull() {
        assertThat(teacherMapper.toEntity((TeacherDto) null)).isNull();
    }

    @Test
    void toDto_list_mapsAllElements() {
        Teacher teacher = Teacher.builder().id(1L).firstName("Jane").lastName("Doe").build();

        List<TeacherDto> dtos = teacherMapper.toDto(List.of(teacher));

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void toDto_list_null_returnsNull() {
        assertThat(teacherMapper.toDto((List<Teacher>) null)).isNull();
    }

    @Test
    void toEntity_list_mapsAllElements() {
        TeacherDto dto = new TeacherDto(1L, "Doe", "Jane", null, null);

        List<Teacher> teachers = teacherMapper.toEntity(List.of(dto));

        assertThat(teachers).hasSize(1);
        assertThat(teachers.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void toEntity_list_null_returnsNull() {
        assertThat(teacherMapper.toEntity((List<TeacherDto>) null)).isNull();
    }
}