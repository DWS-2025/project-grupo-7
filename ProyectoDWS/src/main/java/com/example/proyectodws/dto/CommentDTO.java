package com.example.proyectodws.dto;

import java.sql.Date;

// Data Transfer Object for comments.
public record CommentDTO(
        Long id,
        String text,
        Date createdAt) {
}
