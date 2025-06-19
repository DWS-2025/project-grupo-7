package com.example.proyectodws.dto;

import java.sql.Date;

// Data Transfer Object for comments.
public record CommentWithIdsDTO(
        Long id,
        String text,
        Long userId,
        Long courseId,
        Date createdAt) {
}
