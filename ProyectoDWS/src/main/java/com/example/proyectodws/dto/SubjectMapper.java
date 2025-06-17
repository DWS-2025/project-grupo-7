package com.example.proyectodws.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.proyectodws.entities.Subject;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    SubjectDTO toDTO(Subject subject);

    List<SubjectDTO> toDTOs(Collection<Subject> subjects);

    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "imageFile", ignore = true)
    @Mapping(target = "imageData", ignore = true)
    Subject toDomain(SubjectDTO subjectDTO);
}
