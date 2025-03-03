package com.example.proyectodws.Controllers;

import com.example.proyectodws.Entities.Subject;
import com.example.proyectodws.Service.ImageService;
import com.example.proyectodws.Service.ClassService;
import com.example.proyectodws.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

//this code is for subjects
@Controller
public class ClassController {
    private static final String CLASS_FOLDER = "classes";

    @Autowired
    private ClassService classService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;
    // Return all courses
    @GetMapping("/")
    public String showSubjects(Model model, HttpSession session) {

        model.addAttribute("posts", classService.findAll()); // add the list of subjects to the model
        return "index";
    }

    @GetMapping("/aboutUS")
    public String aboutUs() {
        return "aboutUS";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/ds")
    public String ds() {
        return "ds";
    }

    @GetMapping("/maths")
    public String maths() {
        return "maths";
    }

    @GetMapping("/physics")
    public String physics() {
        return "physics";
    }

    /*@GetMapping("/subject/new")
    public String newSubject() {
        return "new_subject";
    }
    // Add new subject
    @PostMapping("/subject/new")
    public String newPost(Model model, Subject class1, MultipartFile image) throws IOException {

        classService.save(class1);

        imageService.saveImage(CLASS_FOLDER, class1.getId(), image); // saves the image upload by the user

        userService.incNumSubjects();

        model.addAttribute("numClasses", userService.getNumSubjects());

        return "saved_subject";
    }
    @GetMapping("/subject/{id}")
    public String showPost(Model model, @PathVariable long id) {
        Subject class1 = classService.findById(id);

        if (class1 == null) {
            return "errorScreens/Error404"; // error page
        }

        model.addAttribute("class", class1);
        return "show_subject";
    }
    // Return the image for user
    @GetMapping("/subject/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable int id) throws MalformedURLException {

        return imageService.createResponseFromImage(CLASS_FOLDER, id); // responde entity containing the image
    }
    @GetMapping("/subject/{id}/delete")
    public String deletePost(Model model, @PathVariable long id) throws IOException {

        classService.deleteById(id);

        imageService.deleteImage(CLASS_FOLDER, id);
        userService.disNumSubjects();

        return "deleted_subject";
    }
    @GetMapping("/subject/{id}/courses")
    public String showCoursesForSubject(Model model, @PathVariable long id) {
        Subject class1 = classService.findById(id);
        if (class1 == null) {
            return "errorScreens/Error404"; //
        }

        model.addAttribute("class", class1);
        return "show_courses_for_subject";
    }*/

}