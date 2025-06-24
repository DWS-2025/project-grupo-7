package com.example.proyectodws.controllers;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.NewUserRequestDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.service.MediaService;
import com.example.proyectodws.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private MediaService mediaService;

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

        UserDTO oldUser = userService.getLoggedUserDTO();

        if (!oldUser.username().equals(newUserRequestCleaned.username())) {
            // If username exists, throw exception
            UserDTO existingUser = userService.getUserByUsername(newUserRequestCleaned.username());
            if (existingUser != null && existingUser.id() != oldUser.id()) {
                return "redirect:/profile/edit?error=username_taken";
            }
        }

        String hashedPassword = null;
        if (newUserRequestCleaned.password() != null && !newUserRequestCleaned.password().isEmpty()) {
            hashedPassword = new BCryptPasswordEncoder().encode(newUserRequestCleaned.password());
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
                newUserRequestCleaned.first_name(),
                newUserRequestCleaned.last_name(),
                newUserRequestCleaned.username(),
                hashedPassword,
                imageName,
                oldUser.roles(),
                oldUser.courses()
        );

        userService.updateUser(oldUser.id(), newUser);

        return "profile/profile_edited";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(HttpServletResponse response) {

        Long userId = userService.getLoggedUserDTO().id();
        SecurityContextHolder.clearContext();
        userService.deleteUser(userId);

        return "profile/profile_deleted";
    }
}