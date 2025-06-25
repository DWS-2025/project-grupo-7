package com.example.proyectodws.rest;

import java.io.IOException;
import java.sql.SQLException;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.proyectodws.api.GenericResponse;
import com.example.proyectodws.api.UserResponse;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.dto.UserWithoutPasswordDTO;
import com.example.proyectodws.security.jwt.AuthResponse;
import com.example.proyectodws.security.jwt.AuthResponse.Status;
import com.example.proyectodws.security.jwt.UserLoginService;
import com.example.proyectodws.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/profile")
public class ProfileRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserLoginService userLoginService;

    @PutMapping(consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestPart String first_name,
            @RequestPart String last_name,
            @RequestPart String username,
            @RequestPart(required = false) String password,
            @RequestPart(required = false) MultipartFile image) {

        // Use jsoup to clean the input values.
        first_name = Jsoup.clean(first_name, "", Safelist.none());
        last_name = Jsoup.clean(last_name, "", Safelist.none());
        username = Jsoup.clean(username, "", Safelist.none());
        password = Jsoup.clean(password, "", Safelist.none());

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

        UserDTO oldUser = userService.getLoggedUserDTO();

        if (oldUser == null) {
            return ResponseEntity.status(404).body(new UserResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }

        if (username != oldUser.username()) {
            UserDTO existingUser = userService.getUserByUsername(username);
            if (existingUser != null && existingUser.id() != oldUser.id()) {
                return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre de usuario ya existe", 400), null));
            }
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
                null,
                null, null);

        // Save user
        UserDTO updatedUser;
        if (image != null && !image.isEmpty()) {
            try {
                updatedUser = userService.updateWithImage(oldUser.id(), newUser, image);
            } catch (IOException e) {
                return ResponseEntity.status(500).body(new UserResponse(new GenericResponse("Error al guardar la imagen", 500), null));
            } catch (SQLException e) {
                return ResponseEntity.status(500).body(new UserResponse(new GenericResponse("Error al guardar la imagen", 500), null));
            }
        } else {
            updatedUser = userService.updateUser(oldUser.id(), newUser);
        }

        UserWithoutPasswordDTO userWithoutPassword = new UserWithoutPasswordDTO(updatedUser.id(), updatedUser.first_name(), updatedUser.last_name(), updatedUser.username());

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario actualizado correctamente", 200), userWithoutPassword));
    }

    @DeleteMapping()
    public ResponseEntity<AuthResponse> deleteProfile(HttpServletResponse response) {
        Long userId = userService.getLoggedUserDTO().id();
        String result = userLoginService.logout(response);
        userService.deleteUser(userId);
        return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, result));
    }
}