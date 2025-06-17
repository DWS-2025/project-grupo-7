package com.example.proyectodws.dto;

public record CommentWithRelationsDTO(
        Long id,
        String text,
        UserDTO user,
        CourseDTO course
) {

}
