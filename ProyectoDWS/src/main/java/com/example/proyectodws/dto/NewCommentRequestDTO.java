package com.example.proyectodws.dto;

public record NewCommentRequestDTO(
        Long courseId,
        String text) {
}