package com.example.proyectodws.api;

import com.example.proyectodws.dto.SubjectDTO;

public record SubjectResponse(
        GenericResponse result,
        SubjectDTO subject
) {

}