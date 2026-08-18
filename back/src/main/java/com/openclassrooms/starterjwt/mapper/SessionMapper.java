package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", imports = {Collectors.class, Collections.class})
public interface SessionMapper extends EntityMapper<SessionDto, Session> {


    @Mapping(source = "description", target = "description")
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Override
    Session toEntity(SessionDto sessionDto);




    @Mapping(source = "description", target = "description")
    @Mapping(source = "session.teacher.id", target = "teacher_id")
    @Mapping(target = "users", expression = "java(session.getUsers() == null ? Collections.emptyList() : session.getUsers().stream().map(u -> u.getId()).collect(Collectors.toList()))")
    @Override
   SessionDto toDto(Session session);
}