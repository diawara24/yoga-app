package com.openclassrooms.starterjwt.repository;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    void saveAndFindByIdReturnsSession() {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "Jane", null, null));
        Session session = new Session(null, "Morning Yoga", new Date(0), "A relaxing session",
                teacher, new ArrayList<>(), null, null);

        Session saved = sessionRepository.save(session);

        assertThat(sessionRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(Session::getName)
                .isEqualTo("Morning Yoga");
    }
}