package com.example.proyectodws.api;

import java.util.List;

import com.example.proyectodws.dto.CommentWithIdsDTO;

public record CommentsResponse(
        GenericResponse result,
        List<CommentWithIdsDTO> comments
) {

}