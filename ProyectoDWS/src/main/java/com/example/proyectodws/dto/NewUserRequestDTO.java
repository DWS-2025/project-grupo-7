package com.example.proyectodws.dto;

import org.springframework.web.multipart.MultipartFile;

public record NewUserRequestDTO(
        String first_name,
        String last_name,
        String username,
        String password,
        MultipartFile image
) {

}
