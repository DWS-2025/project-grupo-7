package com.example.proyectodws.dto;

import org.springframework.web.multipart.MultipartFile;

public record NewSubjectRequestDTO(
        String title,
        String text,
        MultipartFile image
) {

}
