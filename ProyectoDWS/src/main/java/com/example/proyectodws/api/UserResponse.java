package com.example.proyectodws.api;

import com.example.proyectodws.dto.UserDTO;

public record UserResponse(
        GenericResponse result,
        UserDTO user
) {

}