package com.example.proyectodws.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.NewUserRequestDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.security.jwt.LoginRequest;
import com.example.proyectodws.security.jwt.UserLoginService;
import com.example.proyectodws.service.MediaService;
import com.example.proyectodws.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private UserLoginService userLoginService;

    @GetMapping("/profile")
    public String getProfilePage(Model model) {
        UserDTO user = userService.getLoggedUserDTO();
        List<CourseDTO> enrolledCourses = user.courses();
        model.addAttribute("user", user);
        model.addAttribute("enrolledCourses", enrolledCourses);
        return "profile/profile";
    }

    @GetMapping("/profile/edit")
    public String getEditProfilePage(Model model) {
        UserDTO user = userService.getLoggedUserDTO();
        model.addAttribute("user", user);
        return "profile/edit_profile";
    }

    @GetMapping("/profile/delete")
    public String getDeleteProfilePage() {
        return "profile/confirm_delete_profile";
    }

    @PostMapping(value = "/profile/edit", consumes = "multipart/form-data")
    public String editProfile(
            NewUserRequestDTO newUserRequest,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpServletResponse response) throws IOException {

        UserDTO oldUser = userService.getLoggedUserDTO();

        // Check if username was changed
        if (!oldUser.username().equals(newUserRequest.username())) {
            // If username exists, throw exception
            if (userService.getUserByUsername(newUserRequest.username()) != null) {
                throw new IllegalArgumentException("Username already exists");
            }
        }

        String hashedPassword = null;
        if (newUserRequest.password() != null) {
            hashedPassword = new BCryptPasswordEncoder().encode(newUserRequest.password());
        }

        String imageName = null;
        if (image != null && !image.isEmpty()) {
            imageName = mediaService.saveImage(image);
        }
        else {
            imageName = oldUser.imageName();
        }

        UserDTO newUser = new UserDTO(
                oldUser.id(),
                newUserRequest.first_name(),
                newUserRequest.last_name(),
                newUserRequest.username(),
                hashedPassword != null ? hashedPassword : oldUser.encodedPassword(),
                imageName,
                oldUser.roles(),
                oldUser.courses()
        );

        userService.saveUser(newUser);

        userLoginService.login(response, new LoginRequest(newUser.username(), newUserRequest.password()));

        return "profile/profile_edited";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(HttpServletResponse response) {

        Long userId = userService.getLoggedUserDTO().id();
        userLoginService.logout(response);
        userService.deleteUser(userId);

        return "profile/profile_deleted";
    }
}
