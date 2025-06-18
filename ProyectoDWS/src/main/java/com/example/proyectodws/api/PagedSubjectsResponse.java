package com.example.proyectodws.api;

import java.util.List;

import com.example.proyectodws.dto.SubjectDTO;

public record PagedSubjectsResponse(
        GenericResponse result,
        List<SubjectDTO> subjects,
        int currentPage,
        int totalPages,
        int subjectsPerPage
) {
}