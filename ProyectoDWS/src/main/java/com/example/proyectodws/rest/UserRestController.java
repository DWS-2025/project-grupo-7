package com.example.proyectodws.rest;

import com.example.proyectodws.api.CoursesResponse;
import com.example.proyectodws.api.GenericResponse;
import com.example.proyectodws.api.UserResponse;
import com.example.proyectodws.api.UsersResponse;
import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.dto.UserWithoutPasswordDTO;
import com.example.proyectodws.service.MediaService;
import com.example.proyectodws.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private MediaService mediaService;

    @GetMapping
    public ResponseEntity<UsersResponse> getAllUsers() {
        List<UserWithoutPasswordDTO> usersWithoutPassword = userService.getAllUsers().stream()
                .map(user -> new UserWithoutPasswordDTO(user.id(), user.first_name(), user.last_name(), user.username(), user.imageName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new UsersResponse(new GenericResponse("Usuarios obtenidos correctamente", 200), usersWithoutPassword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(new UserResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }

        UserWithoutPasswordDTO userWithoutPassword = new UserWithoutPasswordDTO(user.id(), user.first_name(), user.last_name(), user.username(), user.imageName());

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario obtenido correctamente", 200), userWithoutPassword));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        UserDTO user = userService.getLoggedUserDTO();

        UserWithoutPasswordDTO userWithoutPassword = new UserWithoutPasswordDTO(user.id(), user.first_name(), user.last_name(), user.username(), user.imageName());

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario obtenido correctamente", 200), userWithoutPassword));
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
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestPart MultipartFile image,
            @RequestParam(required = false) List<String> roles
    ) {

        if (username == null || username.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre de usuario es requerido", 400), null));
        }

        if (password == null || password.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("La contraseña es requerida", 400), null));
        }

        if (first_name == null || first_name.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre es requerido", 400), null));
        }

        if (last_name == null || last_name.isBlank()) {
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
            imageName = mediaService.saveImage(image);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new UserResponse(new GenericResponse("Error al guardar la imagen", 500), null));
        }

        String hashedPassword = new BCryptPasswordEncoder().encode(password);

        List<String> rolesList = new ArrayList<>();
        if (roles == null || roles.isEmpty()) {
            rolesList.add("USER");
        } else {
            rolesList.addAll(roles);
        }

        UserDTO user = new UserDTO(null, first_name, last_name, username, hashedPassword, imageName, rolesList, null);
        UserDTO createdUser = userService.createUser(user);

        UserWithoutPasswordDTO userWithoutPassword = new UserWithoutPasswordDTO(createdUser.id(), createdUser.first_name(), createdUser.last_name(), createdUser.username(), createdUser.imageName());

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario creado correctamente", 200), userWithoutPassword));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestPart(required = false) MultipartFile image,
            @RequestParam(required = false) List<String> roles) {

        if (first_name == null || first_name.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre es requerido", 400), null));
        }

        if (last_name == null || last_name.isBlank()) {
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

        if (username != oldUser.username()) {
            UserDTO existingUser = userService.getUserByUsername(username);
            if (existingUser != null && existingUser.id() != oldUser.id()) {
                return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre de usuario ya existe", 400), null));
            }
        }

        String imageName = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageName = mediaService.saveImage(image);
            } catch (IOException e) {
                return ResponseEntity.status(500).body(new UserResponse(new GenericResponse("Error al guardar la imagen", 500), null));
            }
        } else {
            imageName = oldUser.imageName();
        }

        String encodedPassword = password != null && !password.isEmpty() ?
                new BCryptPasswordEncoder().encode(password) :
                null;

        UserDTO newUser = new UserDTO(
                oldUser.id(),
                first_name,
                last_name,
                username,
                encodedPassword,
                imageName,
                roles,
                null);

        UserDTO updatedUser = userService.updateUser(oldUser.id(), newUser);

        UserWithoutPasswordDTO userWithoutPassword = new UserWithoutPasswordDTO(updatedUser.id(), updatedUser.first_name(), updatedUser.last_name(), updatedUser.username(), updatedUser.imageName());

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario actualizado correctamente", 200), userWithoutPassword));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse> deleteUser(@PathVariable Long id) {

        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(new GenericResponse("Usuario no encontrado", 404));
        }

        UserDTO loggedUser = userService.getLoggedUserDTO();

        if (loggedUser.id().equals(id)) {
            return ResponseEntity.status(400).body(new GenericResponse("No puedes eliminar tu propio usuario", 400));
        }

        userService.deleteUser(id);

        return ResponseEntity.ok(new GenericResponse("Usuario eliminado correctamente", 200));
    }
}