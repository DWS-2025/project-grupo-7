package com.example.proyectodws.dto;

import java.util.List;

public record UpdateCourseRequestDTO(
        Long id,
        String title,
        String description,
        List<Long> subjects) {
}