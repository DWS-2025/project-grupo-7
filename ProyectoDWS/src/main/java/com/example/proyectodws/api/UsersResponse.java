package com.example.proyectodws.api;

import java.util.List;

import com.example.proyectodws.dto.UserWithoutPasswordDTO;

public record UsersResponse(
        GenericResponse result,
        List<UserWithoutPasswordDTO> users
) {

}