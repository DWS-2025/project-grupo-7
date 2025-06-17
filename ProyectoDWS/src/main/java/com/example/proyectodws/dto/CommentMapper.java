package com.example.proyectodws.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.proyectodws.entities.Comment;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CourseMapper.class})
public interface CommentMapper {

    CommentDTO toDTO(Comment comment);

    List<CommentDTO> toDTOs(Collection<Comment> comments);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "user", ignore = true)
    Comment toDomain(CommentDTO commentDTO);
}
