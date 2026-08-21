package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.SessionRequest;
import com.openclassrooms.starterjwt.dto.response.MessageResponse;

import java.util.List;

public interface SessionService {
    SessionDto create(SessionRequest sessionRequest);

  MessageResponse delete(Long id);

    List<SessionDto> findAll();

    SessionDto getById(Long id);

    SessionDto update(Long id, SessionRequest sessionRequest);

   MessageResponse participate(Long id, Long userId);

   MessageResponse noLongerParticipate(Long id, Long userId);
}
