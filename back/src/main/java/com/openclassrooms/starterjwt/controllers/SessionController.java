package com.openclassrooms.starterjwt.controllers;


import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.services.SessionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/session")
@Log4j2
@AllArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(this.sessionService.getById(id));
    }

    @GetMapping()
    public ResponseEntity<List<SessionDto>> findAll() {
        List<SessionDto> sessions = this.sessionService.findAll();

        return ResponseEntity.ok().body(sessions);
    }

    @PostMapping()
    public ResponseEntity<SessionDto> create(@Valid @RequestBody SessionDto sessionDto) {
        SessionDto created = this.sessionService.create(sessionDto);
        return ResponseEntity.ok().body(created);
    }

    @PutMapping("{id}")
    public ResponseEntity<SessionDto> update(@PathVariable("id") Long id, @Valid @RequestBody SessionDto sessionDto) {
        SessionDto updated = this.sessionService.update(id, sessionDto);
        return ResponseEntity.ok().body(updated);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable("id") Long id) {
        MessageResponse resp = this.sessionService.delete(id);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("{id}/participate/{userId}")
    public ResponseEntity<MessageResponse> participate(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        MessageResponse resp = this.sessionService.participate(id, userId);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("{id}/participate/{userId}")
    public ResponseEntity<MessageResponse> noLongerParticipate(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        MessageResponse resp = this.sessionService.noLongerParticipate(id, userId);
        return ResponseEntity.ok(resp);
    }
}
