package com.example.proyectodws.dto;

import org.springframework.web.multipart.MultipartFile;

public record NewSubjectRequest(
        String title,
        String text,
        MultipartFile image
) {

}
