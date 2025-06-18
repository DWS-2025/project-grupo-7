package com.example.proyectodws.api;

import java.util.List;

import com.example.proyectodws.dto.UserDTO;

public record UsersResponse(
        GenericResponse result,
        List<UserDTO> users
) {

}