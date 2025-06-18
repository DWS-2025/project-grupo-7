package com.example.proyectodws.api;

import java.util.List;

import com.example.proyectodws.dto.CourseDTO;

public record CoursesResponse(
        GenericResponse result,
        List<CourseDTO> courses

) {

}