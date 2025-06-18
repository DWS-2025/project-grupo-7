package com.example.proyectodws.api;

import java.util.List;

import com.example.proyectodws.dto.SubjectDTO;

public record SubjectsResponse(
        GenericResponse result,
        List<SubjectDTO> subjects
) {

}