package com.example.proyectodws.api;

import com.example.proyectodws.dto.CourseDTO;

public record CourseResponse(
        GenericResponse result,
        CourseDTO course
) {

}