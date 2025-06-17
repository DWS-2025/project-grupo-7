package com.example.proyectodws.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.proyectodws.entities.Comment;
import com.example.proyectodws.entities.Course;
import com.example.proyectodws.entities.Subject;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDTO toDTO(Course course);

    List<CourseDTO> toDTOs(Collection<Course> courses);

    Set<CourseDTO> toDTOs(Set<Course> courses);

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "imageFile", ignore = true)
    @Mapping(target = "imageData", ignore = true)
    @Mapping(target = "enrolledStudents", ignore = true)
    Course toDomain(CourseDTO courseDTO);

    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "imageFile", ignore = true)
    @Mapping(target = "imageData", ignore = true)
    Subject toDomain(SubjectDTO subjectDTO);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "user", ignore = true)
    Comment toDomain(CommentDTO commentDTO);
}
