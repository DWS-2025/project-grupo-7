package com.example.proyectodws.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.security.jwt.AuthResponse;
import com.example.proyectodws.security.jwt.AuthResponse.Status;
import com.example.proyectodws.security.jwt.LoginRequest;
import com.example.proyectodws.security.jwt.UserLoginService;
import com.example.proyectodws.service.MediaService;
import com.example.proyectodws.service.UserService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class LoginRestController {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserService userService;

    @Autowired
    private MediaService mediaService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        return userLoginService.login(response, loginRequest);
    }

    @PostMapping(consumes = "multipart/form-data", path = "/register")
    public ResponseEntity<AuthResponse> createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestPart MultipartFile image,
            HttpServletResponse response
    ) {
        //validations
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(400).body(new AuthResponse(Status.FAILURE, "El nombre de usuario es requerido"));
        }
        if (password == null || password.isBlank()) {
            return ResponseEntity.status(400).body(new AuthResponse(Status.FAILURE, "La contraseña es requerida"));
        }
        if (first_name == null || first_name.isBlank()) {
            return ResponseEntity.status(400).body(new AuthResponse(Status.FAILURE, "El nombre es requerido"));
        }
        if (last_name == null || last_name.isBlank()) {
            return ResponseEntity.status(400).body(new AuthResponse(Status.FAILURE, "El apellido es requerido"));
        }
        if (image == null || image.isEmpty()) {
            return ResponseEntity.status(400).body(new AuthResponse(Status.FAILURE, "La imagen es requerida"));
        }
        if (image.getSize() > 1024 * 1024 * 5) {
            return ResponseEntity.status(400).body(new AuthResponse(Status.FAILURE, "La imagen no puede pesar más de 5MB"));
        }
        if (userService.getUserByUsername(username) != null) {
            return ResponseEntity.status(400).body(new AuthResponse(Status.FAILURE, "El nombre de usuario ya existe"));
        }

        String imageName = null;

        try {
            imageName = mediaService.saveImage(image);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new AuthResponse(Status.FAILURE, "Error al guardar la imagen"));
        }

        String hashedPassword = new BCryptPasswordEncoder().encode(password);

        List<String> rolesList = new ArrayList<>();
        rolesList.add("USER");

        UserDTO user = new UserDTO(null, first_name, last_name, username, hashedPassword, imageName, rolesList, null);
        userService.saveUser(user);

        userLoginService.login(response, new LoginRequest(username, password));

        return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, "Usuario creado correctamente"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken, HttpServletResponse response) {

        return userLoginService.refresh(response, refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
        return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userLoginService.logout(response)));
    }
}
