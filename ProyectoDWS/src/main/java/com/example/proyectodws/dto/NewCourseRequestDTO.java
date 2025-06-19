package com.example.proyectodws.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

// Data Transfer Object for new courses.
public record NewCourseRequestDTO(
        String title,
        String description,
        MultipartFile image,
        List<Long> subjects,
        MultipartFile video) {
}
