package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.SessionRequest;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.impl.SessionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionMapper sessionMapper;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Test
    void updateChangesSessionFieldsAndKeepsParticipants() {
        Teacher oldTeacher = new Teacher(1L, "Old", "Teacher", null, null);
        Teacher newTeacher = new Teacher(2L, "New", "Teacher", null, null);
        User participant = User.builder().id(5L).email("user@example.com")
                .lastName("Doe").firstName("Jane").password("password").build();
        Session session = new Session(10L, "Old session", new Date(0), "Old description",
                oldTeacher, List.of(participant), null, null);
        SessionRequest request = new SessionRequest();
        request.setName("Updated session");
        request.setDate(new Date(1000));
        request.setTeacher_id(2L);
        request.setDescription("Updated description");
        SessionDto response = new SessionDto(10L, "Updated session", request.getDate(), 2L,
                "Updated description", List.of(5L), null, null);

        when(sessionRepository.findById(10L)).thenReturn(Optional.of(session));
        when(teacherRepository.findById(2L)).thenReturn(Optional.of(newTeacher));
        when(sessionRepository.save(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(response);

        SessionDto result = sessionService.update(10L, request);

        assertThat(result).isSameAs(response);
        assertThat(session.getName()).isEqualTo("Updated session");
        assertThat(session.getTeacher()).isSameAs(newTeacher);
        assertThat(session.getUsers()).containsExactly(participant);
        verify(sessionRepository).save(session);
    }
}