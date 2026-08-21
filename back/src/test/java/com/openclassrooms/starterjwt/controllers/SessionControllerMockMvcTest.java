package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.SessionRequest;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SessionControllerMockMvcTest {

    private MockMvc mockMvc;

    @Mock
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SessionController(sessionService)).build();
    }

    @Test
    void createReturnsTheCreatedSessionDto() throws Exception {
        SessionDto response = new SessionDto(10L, "Morning Yoga", new Date(0), 2L,
                "A relaxing session", null, null, null);
        when(sessionService.create(any(SessionRequest.class))).thenReturn(response);

        String request = """
                {
                  "name": "Morning Yoga",
                  "date": "1970-01-01T00:00:00.000+00:00",
                  "teacher_id": 2,
                  "description": "A relaxing session"
                }
                """;

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Morning Yoga"));

        verify(sessionService).create(any(SessionRequest.class));
    }

    @Test
    void findByIdReturnsTheSessionDto() throws Exception {
        SessionDto response = new SessionDto(10L, "Morning Yoga", new Date(0), 2L,
                "A relaxing session", null, null, null);
        when(sessionService.getById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/session/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.teacher_id").value(2));
    }
}
