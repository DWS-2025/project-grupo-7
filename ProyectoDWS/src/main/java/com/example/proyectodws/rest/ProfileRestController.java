package com.example.proyectodws.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.proyectodws.api.GenericResponse;
import com.example.proyectodws.api.UserResponse;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.security.jwt.AuthResponse;
import com.example.proyectodws.security.jwt.AuthResponse.Status;
import com.example.proyectodws.security.jwt.UserLoginService;
import com.example.proyectodws.service.MediaService;
import com.example.proyectodws.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/profile")
public class ProfileRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private UserLoginService userLoginService;

    @GetMapping()
    public UserDTO getProfile() {
        return userService.getLoggedUserDTO();
    }

    @PutMapping(consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestParam String name,
            @RequestParam String surname,
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestPart(required = false) MultipartFile image,
            @RequestParam(required = false) List<String> roles) {

        // Validate required fields
        if (name == null || name.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre es requerido", 400), null));
        }

        if (surname == null || surname.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El apellido es requerido", 400), null));
        }

        if (username == null || username.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre de usuario es requerido", 400), null));
        }

        // Validate image
        if (image == null || image.isEmpty()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("La imagen es requerida", 400), null));
        }

        // Validate image size.
        if (image.getSize() > 1024 * 1024 * 5) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("La imagen no puede pesar más de 5MB", 400), null));
        }

        // Get old user
        UserDTO oldUser = userService.getLoggedUserDTO();

        if (oldUser == null) {
            return ResponseEntity.status(404).body(new UserResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }

        // Check if username already exists
        if (username != oldUser.username() && userService.getUserByUsername(username) != null) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre de usuario ya existe", 400), null));
        }

        // Check if user exists
        UserDTO existingUser = userService.getLoggedUserDTO();
        if (existingUser == null) {
            return ResponseEntity.status(404).body(new UserResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }

        //Save image if it exists
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

        UserDTO userToUpdate = new UserDTO(
                oldUser.id(),
                name,
                surname,
                username,
                password != null ? password : oldUser.encodedPassword(),
                imageName,
                roles, null);

        // Save user
        UserDTO updatedUser = userService.saveUser(userToUpdate);

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario actualizado correctamente", 200), updatedUser));
    }

    @DeleteMapping()
    public ResponseEntity<AuthResponse> deleteProfile(HttpServletResponse response) {
        Long userId = userService.getLoggedUserDTO().id();
        String result = userLoginService.logout(response);
        userService.deleteUser(userId);
        return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, result));
    }
}
