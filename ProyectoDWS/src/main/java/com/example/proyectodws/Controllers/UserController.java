package com.example.proyectodws.Controllers;

import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.User;
import org.jsoup.Jsoup;
import com.example.proyectodws.Service.CourseService;
import com.example.proyectodws.Service.ImageService;
import com.example.proyectodws.Service.UserService;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ImageService imageService;


    @GetMapping("/create")
    public String showCreateUserForm() {
        return "create_user";
    }

    // Create new user
    @PostMapping("/create")
    public String createUser(@RequestParam String first_name, @RequestParam String last_name, @RequestParam String username, @RequestParam("image") MultipartFile image) throws IOException {
        User user = new User();
        user.setFirst_name(Jsoup.clean(first_name, Safelist.simpleText().addTags("li","ol","ul")));
        user.setLast_name(Jsoup.clean(last_name, Safelist.simpleText().addTags("li","ol","ul")));
        user.setUsername(Jsoup.clean(username, Safelist.simpleText().addTags("li","ol","ul")));

        imageService.saveImage(image);
        user.setImageName(image.getOriginalFilename());
        userService.createUser(user);
        return "redirect:/users/" + user.getId();
    }

    // Show all the users in the model
    @GetMapping
    public String showUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "show_users";
    }

    // Show info from user with 'id'
    @GetMapping("/{id}")
    public String showUserDetails(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        List<Course> availableCourses = courseService.getAllCourses();
        model.addAttribute("user", user);
        model.addAttribute("availableCourses", availableCourses);
        return "user_details";
    }

    // Delete user with 'id'
    @GetMapping("/{id}/deleted")
    public String deleteUserById(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "deleted_user";
    }

    // Add user with 'userId' to the course with 'courseId'
    @PostMapping("/{id}/enroll")
    public String enrollUserInCourse(@PathVariable("id") Long userId, @RequestParam("courseId") Long courseId) {
        userService.enrollUserInCourse(userId, courseId);
        return "redirect:/users/{id}";
    }


    @GetMapping("/{id}/courses")
    public String showUserCourses(@PathVariable("id") Long id, Model model) {
        // Get the courses associated with the user with the ID userId
        User user = userService.getUserById(id);
        Set<Course> userCourses = user.getCourses();

        // Add courses to the model
        model.addAttribute("courses", userCourses);
        model.addAttribute("nombre", user.getFirst_name());
        model.addAttribute("apellido", user.getLast_name());

        // Return name of the HTML template
        return "my_courses";
    }
}
