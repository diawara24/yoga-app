package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.SessionRequest;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SessionMapperTest {

    private final SessionMapper sessionMapper = Mappers.getMapper(SessionMapper.class);

    @Test
    void toDto_mapsTeacherIdAndUserIds() {
        Teacher teacher = Teacher.builder().id(5L).firstName("A").lastName("B").build();
        User user = User.builder().id(9L).email("a@a.com").firstName("A").lastName("B").password("x").build();
        Session session = Session.builder()
                .id(1L).name("Yoga").date(new Date(0)).description("desc")
                .teacher(teacher).users(List.of(user)).build();

        SessionDto dto = sessionMapper.toDto(session);

        assertThat(dto.getTeacher_id()).isEqualTo(5L);
        assertThat(dto.getUsers()).containsExactly(9L);
    }

    @Test
    void toDto_nullUsers_returnsEmptyList() {
        Session session = Session.builder()
                .id(1L).name("Yoga").date(new Date(0)).description("desc")
                .teacher(null).users(null).build();

        SessionDto dto = sessionMapper.toDto(session);

        assertThat(dto.getUsers()).isEmpty();
        assertThat(dto.getTeacher_id()).isNull();
    }

    @Test
    void toEntity_ignoresTeacherAndUsers() {
        SessionDto dto = new SessionDto(1L, "Yoga", new Date(0), 5L, "desc", List.of(9L), null, null);

        Session session = sessionMapper.toEntity(dto);

        assertThat(session.getName()).isEqualTo("Yoga");
        assertThat(session.getTeacher()).isNull(); // résolu par le service, pas le mapper
        assertThat(session.getUsers()).isNull();
    }

    @Test
    void toEntity_fromSessionDto_null_returnsNull() {
        assertThat(sessionMapper.toEntity((SessionDto) null)).isNull();
    }

    @Test
    void toDto_null_returnsNull() {
        assertThat(sessionMapper.toDto((Session) null)).isNull();
    }

    @Test
    void toEntity_fromSessionRequest_mapsFields() {
        SessionRequest request = SessionRequest.builder()
                .name("Yoga").date(new Date(0)).teacher_id(5L).description("desc").build();

        Session session = sessionMapper.toEntity(request);

        assertThat(session.getName()).isEqualTo("Yoga");
        assertThat(session.getDate()).isEqualTo(new Date(0));
        assertThat(session.getDescription()).isEqualTo("desc");
        assertThat(session.getTeacher()).isNull(); // résolu par le service, pas le mapper
    }

    @Test
    void toEntity_fromSessionRequest_null_returnsNull() {
        assertThat(sessionMapper.toEntity((SessionRequest) null)).isNull();
    }

    @Test
    void toDto_list_mapsAllElements() {
        Session session = Session.builder().id(1L).name("Yoga").date(new Date(0)).description("desc").build();

        List<SessionDto> dtos = sessionMapper.toDto(List.of(session));

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getName()).isEqualTo("Yoga");
    }

    @Test
    void toDto_list_null_returnsNull() {
        assertThat(sessionMapper.toDto((List<Session>) null)).isNull();
    }

    @Test
    void toEntity_list_mapsAllElements() {
        SessionDto dto = new SessionDto(1L, "Yoga", new Date(0), 5L, "desc", List.of(9L), null, null);

        List<Session> sessions = sessionMapper.toEntity(List.of(dto));

        assertThat(sessions).hasSize(1);
        assertThat(sessions.get(0).getName()).isEqualTo("Yoga");
    }

    @Test
    void toEntity_list_null_returnsNull() {
        assertThat(sessionMapper.toEntity((List<SessionDto>) null)).isNull();
    }
}