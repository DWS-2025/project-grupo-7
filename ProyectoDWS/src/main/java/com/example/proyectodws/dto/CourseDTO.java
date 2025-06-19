package com.example.proyectodws.dto;

import java.util.List;

// Data Transfer Object for courses.
public record CourseDTO(
        Long id,
        String title,
        String description,
        String image,
        Boolean isFeatured,
        List<SubjectDTO> subjects,
        String video) {}
