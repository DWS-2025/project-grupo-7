package com.example.proyectodws.controllers;

import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.NewUserRequestDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.service.MediaService;
import com.example.proyectodws.service.UserService;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showUsers(Model model) {
        List<UserDTO> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/users";
    }

    @GetMapping("/{id}")
    public String showUserDetails(@PathVariable("id") Long id, Model model) {
        UserDTO user = userService.getUserById(id);
        List<CourseDTO> enrolledCourses = user.courses();
        model.addAttribute("user", user);
        model.addAttribute("enrolledCourses", enrolledCourses);
        return "users/user_details";
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        Resource user = userService.getUserImage(id);

        UserDTO userDTO = userService.getUserById(id);
        if (userDTO == null) {
            return ResponseEntity.status(404).build();
        }

        // Check permissions (admin can see all images but user only his own)
        UserDTO loggedUser = userService.getLoggedUserDTO();
        if (!loggedUser.id().equals(id) && !loggedUser.roles().contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(user);
    }

    // Display the user's courses
    @GetMapping("/{id}/courses")
    public String showUserCourses(@PathVariable("id") Long id, Model model) {
        // Get the courses associated with the user with the ID userId
        UserDTO user = userService.getUserById(id);
        List<CourseDTO> userCourses = user.courses();

        // Add courses to the model
        model.addAttribute("enrolledCourses", userCourses);
        model.addAttribute("nombre", user.first_name());
        model.addAttribute("apellido", user.last_name());

        // Return name of the HTML template
        return "courses/my_courses";
    }

    // Display the create user form.
    @GetMapping("/create")
    public String showCreateUserForm() {
        return "users/create_user";
    }

    // Create new user
    @PostMapping("/create")
    public String createUser(NewUserRequestDTO newUserRequest, @RequestParam("image") MultipartFile image) throws IOException {

        // Use jsoup to clean the input values.
        String firstName = Jsoup.clean(newUserRequest.first_name(), "", Safelist.none());
        String lastName = Jsoup.clean(newUserRequest.last_name(), "", Safelist.none());
        String username = Jsoup.clean(newUserRequest.username(), "", Safelist.none());
        String password = Jsoup.clean(newUserRequest.password(), "", Safelist.none());

        UserDTO user = new UserDTO(
                null,
                firstName,
                lastName,
                username,
                password,
                image.getOriginalFilename(),
                null,
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
        }
        else {
            userService.createUser(user);
        }

        return "redirect:/users/" + user.id();
    }

    // Delete user by 'id'
    @PostMapping("/{id}/delete")
    public String deleteUserById(@PathVariable("id") Long id) {
        UserDTO loggedUser = userService.getLoggedUserDTO();
        UserDTO user = userService.getUserById(id);

        if (loggedUser.id().equals(id)) {
            return "errorScreens/error403";
        }

        if (user.roles().contains("ADMIN")) {
            return "errorScreens/error403";
        }

        userService.deleteUser(id);
        return "users/user_deleted";
    }
}