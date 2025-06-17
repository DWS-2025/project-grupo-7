package com.example.proyectodws.rest;

import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        return ResponseEntity.ok(userService.saveUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<UserDTO> enrollUserInCourse(@PathVariable Long id, @RequestParam Long courseId) {
        userService.enrollUserInCourse(id, courseId);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<CourseDTO>> getUserCourses(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user.courses());
    }
}