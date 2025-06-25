package com.example.proyectodws.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.proyectodws.service.MediaService;
import com.example.proyectodws.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

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

        // Use jsoup to clean the input values.
        String firstName = Jsoup.clean(newUserRequest.first_name(), "", Safelist.none());
        String lastName = Jsoup.clean(newUserRequest.last_name(), "", Safelist.none());
        String username = Jsoup.clean(newUserRequest.username(), "", Safelist.none());
        String password = Jsoup.clean(newUserRequest.password(), "", Safelist.none());

        NewUserRequestDTO newUserRequestCleaned = new NewUserRequestDTO(
                firstName,
                lastName,
                username,
                password,
                image
        );

        // Check if username already exists
        if (userService.getUserByUsername(newUserRequestCleaned.username()) != null) {
            return "redirect:/register?error=username_taken";
        }

        String hashedPassword = new BCryptPasswordEncoder().encode(newUserRequestCleaned.password());

        String imageName = mediaService.saveImage(image);
        List<String> roles = new ArrayList<>(Arrays.asList("USER"));

        UserDTO user = new UserDTO(
                null,
                newUserRequestCleaned.first_name(),
                newUserRequestCleaned.last_name(),
                newUserRequestCleaned.username(),
                hashedPassword,
                imageName,
                roles,
                null
        );

        if (image != null && !image.isEmpty()) {
            try {
                userService.createWithImage(user, image);
            } catch (IOException e) {
                return "errorScreens/error500";
            } catch (SQLException e) {
                return "errorScreens/error500";
            }
        } else {
            userService.createUser(user);
        }

        return "redirect:/login";
    }
}