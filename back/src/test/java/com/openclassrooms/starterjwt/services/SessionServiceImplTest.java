package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.SessionRequest;
import com.openclassrooms.starterjwt.dto.response.MessageResponse;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock private SessionMapper sessionMapper;
    @Mock private SessionRepository sessionRepository;
    @Mock private UserRepository userRepository;
    @Mock private TeacherRepository teacherRepository;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Test
    void delete_existingSession_deletesAndReturnsMessage() {
        when(sessionRepository.existsById(1L)).thenReturn(true);
        when(sessionRepository.getReferenceById(1L)).thenReturn(mock(Session.class));

        MessageResponse result = sessionService.delete(1L);

        verify(sessionRepository).delete(any(Session.class));
        assertThat(result.message()).isEqualTo("Session supprimé");
    }

    @Test
    void delete_unknownSession_throwsNotFoundException() {
        when(sessionRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> sessionService.delete(99L))
                .isInstanceOf(NotFoundException.class);

        verify(sessionRepository, never()).deleteById(any());
    }

    @Test
    void findAll_returnsMappedSessions() {
        Session session = mock(Session.class);
        SessionDto dto = mock(SessionDto.class);
        when(sessionRepository.findAll()).thenReturn(List.of(session));
        when(sessionMapper.toDto(session)).thenReturn(dto);

        assertThat(sessionService.findAll()).containsExactly(dto);
    }

    @Test
    void getById_existingSession_returnsDto() {
        Session session = mock(Session.class);
        SessionDto dto = mock(SessionDto.class);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionMapper.toDto(session)).thenReturn(dto);

        assertThat(sessionService.getById(1L)).isSameAs(dto);
    }

    @Test
    void getById_unknownSession_throwsNotFoundException() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.getById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void participate_newParticipant_addsUserAndSaves() {
        User user = User.builder().id(2L).email("a@a.com").firstName("A").lastName("B").password("pwd").build();
        Session session = new Session(1L, "Yoga", null, "desc", null, new java.util.ArrayList<>(), null, null);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        MessageResponse result = sessionService.participate(1L, 2L);

        assertThat(session.getUsers()).containsExactly(user);
        assertThat(result.message()).isEqualTo("Participation confirmée");
        verify(sessionRepository).save(session);
    }

    @Test
    void participate_alreadyParticipating_throwsBadRequestException() {
        User user = User.builder().id(2L).email("a@a.com").firstName("A").lastName("B").password("pwd").build();
        Session session = new Session(1L, "Yoga", null, "desc", null, List.of(user), null, null);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> sessionService.participate(1L, 2L))
                .isInstanceOf(BadRequestException.class);

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void participate_unknownUser_throwsNotFoundException() {
        Session session = new Session(1L, "Yoga", null, "desc", null, new java.util.ArrayList<>(), null, null);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.participate(1L, 2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void noLongerParticipate_participatingUser_removesUser() {
        User user = User.builder().id(2L).email("a@a.com").firstName("A").lastName("B").password("pwd").build();
        Session session = new Session(1L, "Yoga", null, "desc", null,
                new java.util.ArrayList<>(List.of(user)), null, null);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        MessageResponse result = sessionService.noLongerParticipate(1L, 2L);

        assertThat(session.getUsers()).isEmpty();
        assertThat(result.message()).isEqualTo("Participation retirer");
    }

    @Test
    void noLongerParticipate_notParticipating_throwsBadRequestException() {
        Session session = new Session(1L, "Yoga", null, "desc", null, List.of(), null, null);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 99L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void noLongerParticipate_unknownSession_throwsNotFoundException() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_happyPath_setsTeacherAndSavesSession() {
        Teacher teacher = Teacher.builder().id(5L).firstName("A").lastName("B").build();
        SessionRequest request = SessionRequest.builder()
                .name("Yoga").date(new java.util.Date(0)).teacher_id(5L).description("desc").build();
        Session mappedSession = Session.builder().name("Yoga").description("desc").build();
        Session savedSession = Session.builder().id(1L).name("Yoga").description("desc").teacher(teacher).build();
        SessionDto dto = mock(SessionDto.class);

        when(teacherRepository.findById(5L)).thenReturn(Optional.of(teacher));
        when(sessionMapper.toEntity(request)).thenReturn(mappedSession);
        when(sessionRepository.save(mappedSession)).thenReturn(savedSession);
        when(sessionMapper.toDto(savedSession)).thenReturn(dto);

        SessionDto result = sessionService.create(request);

        assertThat(result).isSameAs(dto);
        assertThat(mappedSession.getTeacher()).isSameAs(teacher);
        assertThat(mappedSession.getUsers()).isEmpty();
    }

    @Test
    void create_unknownTeacher_throwsNotFoundException() {
        SessionRequest request = SessionRequest.builder()
                .name("Yoga").date(new java.util.Date(0)).teacher_id(99L).description("desc").build();
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.create(request))
                .isInstanceOf(NotFoundException.class);

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void update_happyPath_updatesFieldsAndSaves() {
        Session existing = Session.builder().id(1L).name("Old").description("old desc").build();
        Teacher teacher = Teacher.builder().id(5L).firstName("A").lastName("B").build();
        SessionRequest request = SessionRequest.builder()
                .name("New").date(new java.util.Date(0)).teacher_id(5L).description("new desc").build();
        SessionDto dto = mock(SessionDto.class);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(teacherRepository.existsById(5L)).thenReturn(true);
        when(teacherRepository.getReferenceById(5L)).thenReturn(teacher);
        when(sessionRepository.save(existing)).thenReturn(existing);
        when(sessionMapper.toDto(existing)).thenReturn(dto);

        SessionDto result = sessionService.update(1L, request);

        assertThat(result).isSameAs(dto);
        assertThat(existing.getName()).isEqualTo("New");
        assertThat(existing.getDescription()).isEqualTo("new desc");
        assertThat(existing.getTeacher()).isSameAs(teacher);
    }

    @Test
    void update_unknownSession_throwsNotFoundException() {
        SessionRequest request = SessionRequest.builder()
                .name("New").date(new java.util.Date(0)).teacher_id(5L).description("new desc").build();
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.update(1L, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_unknownTeacher_throwsNotFoundException() {
        Session existing = Session.builder().id(1L).name("Old").description("old desc").build();
        SessionRequest request = SessionRequest.builder()
                .name("New").date(new java.util.Date(0)).teacher_id(99L).description("new desc").build();

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(teacherRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> sessionService.update(1L, request))
                .isInstanceOf(NotFoundException.class);

        verify(sessionRepository, never()).save(any());
    }
}