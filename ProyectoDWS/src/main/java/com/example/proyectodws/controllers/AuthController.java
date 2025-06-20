package com.example.proyectodws.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.proyectodws.dto.NewUserRequestDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.security.jwt.AuthResponse;
import com.example.proyectodws.security.jwt.LoginRequest;
import com.example.proyectodws.security.jwt.UserLoginService;
import com.example.proyectodws.service.MediaService;
import com.example.proyectodws.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private MediaService mediaService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/loginerror")
    public String loginerror() {
        return "auth/loginerror";
    }

    @GetMapping("/register")
    public String register(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            switch (error) {
                case "username_taken":
                    model.addAttribute("error", "El nombre de usuario ya está en uso");
                    break;
                default:
                    model.addAttribute("error", "Error al crear la cuenta");
            }
        }

        return "auth/register";
    }

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public String register(
            @Validated NewUserRequestDTO newUserRequest,
            @RequestParam("image") MultipartFile image,
            HttpServletResponse response) throws IOException {

        // Check if username already exists
        if (userService.getUserByUsername(newUserRequest.username()) != null) {
            return "redirect:/register?error=username_taken";
        }

        String hashedPassword = new BCryptPasswordEncoder().encode(newUserRequest.password());

        String imageName = mediaService.saveImage(image);
        List<String> roles = new ArrayList<>(Arrays.asList("USER"));

        UserDTO user = new UserDTO(
                null,
                newUserRequest.first_name(),
                newUserRequest.last_name(),
                newUserRequest.username(),
                hashedPassword,
                imageName,
                roles,
                null
        );

        userService.saveUser(user);

        LoginRequest loginRequest = new LoginRequest(user.username(), newUserRequest.password());

        ResponseEntity<AuthResponse> responseEntity = userLoginService.login(response, loginRequest);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return "redirect:/";
        } else {
            return "redirect:/loginerror";
        }
    }
}
