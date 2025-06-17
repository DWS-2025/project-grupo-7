package com.example.proyectodws.rest;

import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.SubjectDTO;
import com.example.proyectodws.service.CourseService;
import com.example.proyectodws.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseRestController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        return course != null ? ResponseEntity.ok(course) : ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<CourseDTO> createCourseByParams(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) List<Long> subjectIds,
            @RequestPart(required = false) MultipartFile image) throws IOException, SQLException {

        List<SubjectDTO> subjects = subjectIds.stream()
                .map(id -> subjectService.getSubjectById(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        CourseDTO course = new CourseDTO(
                null,
                title,
                description,
                image.getOriginalFilename(),
                false,
                subjects
        );

        CourseDTO saved = null;
        if (image != null && !image.isEmpty()) {
            saved = courseService.createWithImage(course, image);
        }
        else {
            saved = courseService.saveCourse(course);
        }

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourseByParams(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile image) throws IOException, SQLException {

        CourseDTO newCourse = new CourseDTO(
                id,
                title,
                description,
                image.getOriginalFilename(),
                false,
                null
        );

        CourseDTO updated = courseService.updateCourse(id, newCourse);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
