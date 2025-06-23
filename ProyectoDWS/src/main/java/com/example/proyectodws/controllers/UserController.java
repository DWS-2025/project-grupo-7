package com.example.proyectodws.controllers;

import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.NewUserRequestDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.service.MediaService;
import com.example.proyectodws.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MediaService mediaService;

    // Display all the users
    @GetMapping
    public String showUsers(Model model) {
        List<UserDTO> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/users";
    }

    // Display info from user by 'id'
    @GetMapping("/{id}")
    public String showUserDetails(@PathVariable("id") Long id, Model model) {
        UserDTO user = userService.getUserById(id);
        List<CourseDTO> enrolledCourses = user.courses();
        model.addAttribute("user", user);
        model.addAttribute("enrolledCourses", enrolledCourses);
        return "users/user_details";
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

        UserDTO user = new UserDTO(
                null,
                newUserRequest.first_name(),
                newUserRequest.last_name(),
                newUserRequest.username(),
                newUserRequest.password(),
                image.getOriginalFilename(),
                null,
                null
        );

        mediaService.saveImage(image);
        userService.createUser(user);
        return "redirect:/users/" + user.id();
    }

    // Delete user by 'id'
    @PostMapping("/{id}/delete")
    public String deleteUserById(@PathVariable("id") Long id) {
        UserDTO loggedUser = userService.getLoggedUserDTO();
        if (loggedUser.id().equals(id)) {
            return "errorScreens/error403";
        }

        userService.deleteUser(id);
        return "users/user_deleted";
    }
}