package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;

import java.util.List;

public interface SessionService {
    SessionDto create(SessionDto sessionDto);

  MessageResponse delete(Long id);

    List<SessionDto> findAll();

    SessionDto getById(Long id);

    SessionDto update(Long id, SessionDto sessionDto);

   MessageResponse participate(Long id, Long userId);

   MessageResponse noLongerParticipate(Long id, Long userId);
}
