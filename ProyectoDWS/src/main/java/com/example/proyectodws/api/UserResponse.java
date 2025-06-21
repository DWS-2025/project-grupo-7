package com.example.proyectodws.api;

import com.example.proyectodws.dto.UserWithoutPasswordDTO;

public record UserResponse(
        GenericResponse result,
        UserWithoutPasswordDTO user
) {

}