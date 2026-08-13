package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.services.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("/{id}")
    public ResponseEntity<TeacherDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(this.teacherService.findById(id));
    }

    @GetMapping()
    public ResponseEntity<List<TeacherDto>> findAll() {
        List<TeacherDto> teachers = this.teacherService.findAll();

        return ResponseEntity.ok().body(teachers);
    }
}
