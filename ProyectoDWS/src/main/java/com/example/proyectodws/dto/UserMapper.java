package com.example.proyectodws.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;

import com.example.proyectodws.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    List<UserDTO> toDTOs(Collection<User> users);

    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "comments", ignore = true)
    User toDomain(UserDTO userDTO);
}