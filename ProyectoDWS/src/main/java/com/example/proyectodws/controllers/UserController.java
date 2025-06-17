package com.example.proyectodws.controllers;

import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.NewUserRequestDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.service.ImageService;
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
    private ImageService imageService;


    @GetMapping("/create")
    public String showCreateUserForm() {
        return "users/create_user";
    }

    //create new user
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

        imageService.saveImage(image);
        userService.saveUser(user);
        return "redirect:/users/" + user.id();
    }

    //show all the users in the model
    @GetMapping
    public String showUsers(Model model) {
        List<UserDTO> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/show_users";
    }

    //show info from user with 'id'
    @GetMapping("/{id}")
    public String showUserDetails(@PathVariable("id") Long id, Model model) {
        UserDTO user = userService.getUserById(id);
        List<CourseDTO> availableCourses = user.courses();
        model.addAttribute("user", user);
        model.addAttribute("availableCourses", availableCourses);
        return "users/user_details";
    }

    //delete user with 'id'
    @GetMapping("/{id}/deleted")
    public String deleteUserById(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "users/deleted_user";
    }

    @GetMapping("/{id}/courses")
    public String showUserCourses(@PathVariable("id") Long id, Model model) {
        //get courses associated with ID userId
        UserDTO user = userService.getUserById(id);
        List<CourseDTO> userCourses = user.courses();

        //add courses to the model
        model.addAttribute("enrolledCourses", userCourses);
        model.addAttribute("nombre", user.first_name());
        model.addAttribute("apellido", user.last_name());


        return "courses/my_courses";
    }
}
