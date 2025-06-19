package com.example.proyectodws.dto;

import java.util.List;

// Data Transfer Object for subjects.
public record SubjectDTOWithCourses(
        Long id,
        String title,
        String text,
        String image,
        List<CourseDTO> associatedCourses) {}
