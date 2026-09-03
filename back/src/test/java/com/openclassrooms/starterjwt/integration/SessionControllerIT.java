package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionRequest;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "user@example.com") // toutes les routes /api/session sont protégées
class SessionControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private SessionRepository sessionRepository;
    @Autowired private TeacherRepository teacherRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ObjectMapper objectMapper;

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        teacher = teacherRepository.save(Teacher.builder()
                .firstName("Jane").lastName("Doe").build());
    }

    // Nettoyage après chaque test : cette classe partage la même base H2
    // embarquée que les autres suites *ControllerIT au sein du même run
    // failsafe. Sans ce nettoyage, une session ou un utilisateur créé ici
    // et non supprimé peut violer une contrainte de clé étrangère lors du
    // deleteAll() effectué par le setUp() de TeacherControllerIT/UserControllerIT.
    @AfterEach
    void tearDown() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    void createThenGetSession_endToEnd() throws Exception {
        SessionRequest request = SessionRequest.builder()
                .name("Yoga débutant")
                .date(new Date())
                .teacher_id(teacher.getId())
                .description("desc")
                .build();

        String response = mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga débutant"))
                .andReturn().getResponse().getContentAsString();

        Long createdId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/session/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teacher_id").value(teacher.getId()));
    }

    @Test
    void create_unknownTeacher_returns404() throws Exception {
        SessionRequest request = SessionRequest.builder()
                .name("Yoga")
                .date(new Date())
                .teacher_id(999L)
                .description("desc")
                .build();

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_unknownSession_returns404() throws Exception {
        mockMvc.perform(get("/api/session/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existingSession_thenGetReturns404() throws Exception {
        SessionRequest request = SessionRequest.builder()
                .name("Yoga")
                .date(new Date())
                .teacher_id(teacher.getId())
                .description("desc")
                .build();

        String response = mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        Long createdId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/session/" + createdId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/session/" + createdId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_returnsAllSessions() throws Exception {
        SessionRequest request = SessionRequest.builder()
                .name("Yoga").date(new Date()).teacher_id(teacher.getId()).description("desc").build();
        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Yoga"));
    }

    @Test
    void update_existingSession_returnsUpdatedSession() throws Exception {
        SessionRequest createRequest = SessionRequest.builder()
                .name("Yoga").date(new Date()).teacher_id(teacher.getId()).description("desc").build();
        String response = mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();
        Long createdId = objectMapper.readTree(response).get("id").asLong();

        SessionRequest updateRequest = SessionRequest.builder()
                .name("Yoga avancé").date(new Date()).teacher_id(teacher.getId()).description("desc mis à jour").build();

        mockMvc.perform(put("/api/session/" + createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga avancé"))
                .andExpect(jsonPath("$.description").value("desc mis à jour"));
    }

    @Test
    void update_unknownSession_returns404() throws Exception {
        SessionRequest updateRequest = SessionRequest.builder()
                .name("Yoga").date(new Date()).teacher_id(teacher.getId()).description("desc").build();

        mockMvc.perform(put("/api/session/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void participateThenNoLongerParticipate_endToEnd() throws Exception {
        User user = userRepository.save(User.builder()
                .email("participant@example.com").firstName("Jane").lastName("Doe")
                .password("encoded-password").build());

        SessionRequest request = SessionRequest.builder()
                .name("Yoga").date(new Date()).teacher_id(teacher.getId()).description("desc").build();
        String response = mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        Long createdId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(post("/api/session/" + createdId + "/participate/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Participation confirmée"));

        mockMvc.perform(get("/api/session/" + createdId))
                .andExpect(jsonPath("$.users[0]").value(user.getId()));

        mockMvc.perform(delete("/api/session/" + createdId + "/participate/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Participation retirer"));

        mockMvc.perform(get("/api/session/" + createdId))
                .andExpect(jsonPath("$.users").isEmpty());
    }
}