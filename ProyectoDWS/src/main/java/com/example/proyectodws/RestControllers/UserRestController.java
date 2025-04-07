package com.example.proyectodws.RestControllers;

import com.example.proyectodws.Entities.User;
import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Service.UserService;
import com.example.proyectodws.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<User> enrollUserInCourse(@PathVariable Long id, @RequestParam Long courseId) {
        userService.enrollUserInCourse(id, courseId);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<Set<Course>> getUserCourses(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user.getCourses());
    }
}