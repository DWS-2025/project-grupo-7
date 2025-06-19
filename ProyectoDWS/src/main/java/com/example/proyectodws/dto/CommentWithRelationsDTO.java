package com.example.proyectodws.dto;

import java.sql.Date;

// Data Transfer Object for comments with relations.
public record CommentWithRelationsDTO(
        Long id,
        String text,
        UserDTO user,
        CourseDTO course,
        Date createdAt
) {

}
