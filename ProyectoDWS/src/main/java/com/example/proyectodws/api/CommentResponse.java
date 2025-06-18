package com.example.proyectodws.api;

import com.example.proyectodws.dto.CommentDTO;

public record CommentResponse(
        GenericResponse result,
        CommentDTO comment
) {

}