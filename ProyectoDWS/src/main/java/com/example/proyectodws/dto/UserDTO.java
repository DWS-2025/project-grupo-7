package com.example.proyectodws.dto;

import java.util.List;

public record UserDTO(
        Long id,
        String first_name,
        String last_name,
        String username,
        String encodedPassword,
        String image,
        List<String> roles,
        List<CourseDTO> courses) {
}