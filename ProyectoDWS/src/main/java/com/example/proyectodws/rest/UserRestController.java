package com.example.proyectodws.rest;

import com.example.proyectodws.api.CoursesResponse;
import com.example.proyectodws.api.GenericResponse;
import com.example.proyectodws.api.UserResponse;
import com.example.proyectodws.api.UsersResponse;
import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.service.CourseService;
import com.example.proyectodws.service.ImageService;
import com.example.proyectodws.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ImageService imageService;

    @GetMapping
    public ResponseEntity<UsersResponse> getAllUsers() {
        return ResponseEntity.ok(new UsersResponse(new GenericResponse("Usuarios obtenidos correctamente", 200), userService.getAllUsers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(new UserResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }
        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario obtenido correctamente", 200), user));
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<CoursesResponse> getUserCourses(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.status(404).body(new CoursesResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }

        List<CourseDTO> courses = user.courses();

        return ResponseEntity.ok(new CoursesResponse(new GenericResponse("Cursos obtenidos correctamente", 200), courses));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String name,
            @RequestParam String surname,
            @RequestPart MultipartFile image,
            @RequestParam(required = false) List<String> roles
    ) {

        if (username == null || username.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre de usuario es requerido", 400), null));
        }

        if (password == null || password.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("La contraseña es requerida", 400), null));
        }

        if (name == null || name.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre es requerido", 400), null));
        }

        if (surname == null || surname.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El apellido es requerido", 400), null));
        }

        if (image == null || image.isEmpty()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("La imagen es requerida", 400), null));
        }

        if (image.getSize() > 1024 * 1024 * 5) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("La imagen no puede pesar más de 5MB", 400), null));
        }

        if (userService.getUserByUsername(username) != null) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre de usuario ya existe", 400), null));
        }

        String imageName = null;

        try {
            imageName = imageService.saveImage(image);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new UserResponse(new GenericResponse("Error al guardar la imagen", 500), null));
        }


        List<String> rolesList = new ArrayList<>();
        if (roles == null || roles.isEmpty()) {
            rolesList.add("USER");
        } else {
            rolesList.addAll(roles);
        }

        UserDTO user = new UserDTO(null, name, surname, username, password, imageName, rolesList, null);
        UserDTO createdUser = userService.saveUser(user);

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario creado correctamente", 200), createdUser));
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<UserResponse> enrollUserInCourse(
            @PathVariable Long id,
            @RequestParam Long courseId
    ) {

        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(new UserResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }

        CourseDTO course = courseService.getCourseById(courseId);
        if (course == null) {
            return ResponseEntity.status(404).body(new UserResponse(new GenericResponse("Curso no encontrado", 404), null));
        }

        if (user.courses().contains(course)) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("Usuario ya inscrito en este curso", 400), null));
        }

        userService.enrollUserInCourse(id, courseId);

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario inscrito en curso correctamente", 200), userService.getUserById(id)));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String surname,
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestPart(required = false) MultipartFile image,
            @RequestParam(required = false) List<String> roles) {

        if (name == null || name.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre es requerido", 400), null));
        }

        if (surname == null || surname.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El apellido es requerido", 400), null));
        }

        if (username == null || username.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre de usuario es requerido", 400), null));
        }

        if (image == null || image.isEmpty()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("La imagen es requerida", 400), null));
        }

        if (image.getSize() > 1024 * 1024 * 5) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("La imagen no puede pesar más de 5MB", 400), null));
        }

        UserDTO oldUser = userService.getUserById(id);

        if (oldUser == null) {
            return ResponseEntity.status(404).body(new UserResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }

        if (username != oldUser.username() && userService.getUserByUsername(username) != null) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre de usuario ya existe", 400), null));
        }

        UserDTO existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.status(404).body(new UserResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }

        String imageName = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageName = imageService.saveImage(image);
            } catch (IOException e) {
                return ResponseEntity.status(500).body(new UserResponse(new GenericResponse("Error al guardar la imagen", 500), null));
            }
        } else {
            imageName = oldUser.imageName();
        }

        UserDTO userToUpdate = new UserDTO(
                oldUser.id(),
                name,
                surname,
                username,
                password != null ? password : oldUser.encodedPassword(),
                imageName,
                roles, null);

        UserDTO updatedUser = userService.saveUser(userToUpdate);

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario actualizado correctamente", 200), updatedUser));
    }

    @PutMapping("/{id}/unenroll")
    public ResponseEntity<UserResponse> unenrollUserFromCourse(
            @PathVariable Long id,
            @RequestParam Long courseId
    ) {
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(new UserResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }

        CourseDTO course = courseService.getCourseById(courseId);
        if (course == null) {
            return ResponseEntity.status(404).body(new UserResponse(new GenericResponse("Curso no encontrado", 404), null));
        }

        if (!user.courses().contains(course)) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("Usuario no inscrito en este curso", 400), null));
        }

        userService.unenrollUserFromCourse(id, courseId);

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario desinscrito del curso correctamente", 200), userService.getUserById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse> deleteUser(@PathVariable Long id) {

        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(new GenericResponse("Usuario no encontrado", 404));
        }

        userService.deleteUser(id);

        return ResponseEntity.ok(new GenericResponse("Usuario eliminado correctamente", 200));
    }
}