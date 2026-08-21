package com.openclassrooms.starterjwt.repository;

import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    void saveAndFindByIdReturnsTeacher() {
        Teacher saved = teacherRepository.save(new Teacher(null, "Doe", "Jane", null, null));

        assertThat(teacherRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(Teacher::getFirstName)
                .isEqualTo("Jane");
    }
}