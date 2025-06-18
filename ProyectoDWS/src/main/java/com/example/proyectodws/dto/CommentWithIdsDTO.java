package com.example.proyectodws.dto;

public record CommentWithIdsDTO(
        Long id,
        String text,
        Long userId,
        Long courseId) {
}