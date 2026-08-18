package com.openclassrooms.starterjwt.services.impl;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SessionServiceImpl implements SessionService {

    private final SessionMapper sessionMapper;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;

    @Override
    public SessionDto create(SessionDto sessionDto) {
        Teacher teacher = this.teacherRepository.findById(sessionDto.getTeacher_id()).orElseThrow(() -> new NotFoundException("error.teacher.not-found", sessionDto.getTeacher_id()));
        Session session = sessionMapper.toEntity(sessionDto);
        session.setTeacher(teacher);
        Session s = this.sessionRepository.save(session);
        return sessionMapper.toDto(s);
    }

    @Override
    public MessageResponse delete(Long id) {
       this.sessionRepository.findById(id).orElseThrow(() -> new NotFoundException("error.session.not-found", id));

        this.sessionRepository.deleteById(id);

        return new MessageResponse("Session supprimé");
    }

    @Override
    public List<SessionDto> findAll() {
        return this.sessionRepository.findAll().stream()
                .map(sessionMapper::toDto)
                .toList();
    }

    @Override
    public SessionDto getById(Long id) {
        Session s =  this.sessionRepository.findById(id).orElseThrow(() ->
                new NotFoundException("error.session.not-found", id));
        return sessionMapper.toDto(s);
    }

    @Override
    public SessionDto update(Long id, SessionDto sessionDto) {
        Session session = sessionMapper.toEntity(sessionDto);
        session.setId(id);
        Session updated = this.sessionRepository.save(session);
        return sessionMapper.toDto(updated);
    }

    @Override
    public MessageResponse participate(Long id, Long userId) {
        Session session = this.sessionRepository.findById(id).orElseThrow(
                () -> new NotFoundException("error.session.not-found", id)
        );

        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("error.user.not-found", userId)
        );

        boolean alreadyParticipate = session.getUsers().stream().anyMatch(o -> o.getId().equals(userId));
        if (alreadyParticipate) {
            throw new BadRequestException("error.detail.bad-request");
        }

        session.getUsers().add(user);
        this.sessionRepository.save(session);
        return new MessageResponse("Participation confirmée");
    }

    @Override
    public MessageResponse noLongerParticipate(Long id, Long userId) {
        Session session = this.sessionRepository.findById(id).orElseThrow(() -> new NotFoundException("error.session.not-found", id));

        boolean alreadyParticipate = session.getUsers().stream().anyMatch(o -> o.getId().equals(userId));
        if (!alreadyParticipate) {
            throw new BadRequestException("error.detail.bad-request");
        }

        session.setUsers(session.getUsers().stream().filter(user -> !user.getId().equals(userId))
                .toList());
        this.sessionRepository.save(session);

        return new MessageResponse("Participation retirer");
    }
}
