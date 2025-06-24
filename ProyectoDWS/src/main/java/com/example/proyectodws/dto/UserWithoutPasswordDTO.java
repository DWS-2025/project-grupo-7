package com.example.proyectodws.dto;

public record UserWithoutPasswordDTO(
        Long id,
        String first_name,
        String last_name,
        String username
) {
}