package com.example.proyectodws.RestControllers;

import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.Subject;
import com.example.proyectodws.Service.CourseService;
import com.example.proyectodws.Service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Course> createCourseByParams(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) Long subjectId) {

        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);

        if (subjectId != null) {
            Subject subject = subjectService.getSubjectById(subjectId);
            if (subject == null) {
                return ResponseEntity.badRequest().build();
            }
            course.setSubject(subject);
        }

        Course saved = courseService.createCourse(course);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourseByParams(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description) {

        Course course = courseService.getCourseById(id);
        if (course == null) return ResponseEntity.notFound().build();

        course.setTitle(title);
        course.setDescription(description);

        Course updated = courseService.createCourse(course);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
