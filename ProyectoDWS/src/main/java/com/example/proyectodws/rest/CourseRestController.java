package com.example.proyectodws.rest;

import com.example.proyectodws.entities.Course;
import com.example.proyectodws.entities.Subject;
import com.example.proyectodws.service.CourseService;
import com.example.proyectodws.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseRestController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return course != null ? ResponseEntity.ok(course) : ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Course> createCourseByParams(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) List<Long> subjectIds,
            @RequestPart(required = false) MultipartFile image) throws IOException, SQLException {

        Course course = new Course(title, description);

        if (subjectIds != null) {
            for (Long subjectId : subjectIds) {
                Subject subject = subjectService.getSubjectById(subjectId);
                if (subject == null) {
                    return ResponseEntity.badRequest().build();
                }
                course.addSubject(subject);
            }
        }

        if (image != null && !image.isEmpty()) {
            course.setImageData(image.getBytes());
        }

        Course saved = courseService.save(course, image);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourseByParams(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile image) throws IOException, SQLException {

        Course course = courseService.getCourseById(id);
        if (course == null) return ResponseEntity.notFound().build();

        course.setTitle(title);
        course.setDescription(description);

        Course updated = courseService.save(course, image);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
