package com.example.proyectodws.Controllers;

import com.example.proyectodws.Entities.Class;
import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Service.ImageService;
import com.example.proyectodws.Service.ClassService;
import com.example.proyectodws.Service.UserSession;
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
import java.util.List;

//this code is for subjects
@Controller
public class ClassController {
    private static final String CLASS_FOLDER = "classes";

    @Autowired
    private ClassService classService;

    @Autowired
    private UserSession userSession;

    @Autowired
    private ImageService imageService;
    // Return all courses
    @GetMapping("/")
    public String showPosts(Model model, HttpSession session) {

        model.addAttribute("posts", classService.findAll()); // add the list of languages to the model
        model.addAttribute("welcome", session.isNew()); // 'welcome' for new user's session
        return "index";
    }
    // Display new language form
    @GetMapping("/language/new")
    public String newPostForm(Model model) {

        model.addAttribute("user", userSession.getUser()); // add the user attribute to the model

        return "new_language";
    }
    // Add new language
    @PostMapping("/language/new")
    public String newPost(Model model, Class class1, MultipartFile image) throws IOException {

        classService.save(class1);

        imageService.saveImage(CLASS_FOLDER, class1.getId(), image); // saves the image upload by the user

        userSession.incNumLanguages(); // increments the number of languages for the user

        model.addAttribute("numClasses", userSession.getNumLanguages()); // adds the number of languages to the model

        return "saved_language";
    }
    // Display a view of the language
    @GetMapping("/language/{id}")
    public String showPost(Model model, @PathVariable long id) {

        Class class1 = classService.findById(id);
        // return 404
        if (class1 == null) {
            return "page404";
        }

        model.addAttribute("class", class1); // adds the language attribute to the model

        return "show_language";
    }
    // Return the image for user
    @GetMapping("/language/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable int id) throws MalformedURLException {

        return imageService.createResponseFromImage(CLASS_FOLDER, id); // responde entity containing the image
    }
    @GetMapping("/language/{id}/delete")
    public String deletePost(Model model, @PathVariable long id) throws IOException {

        classService.deleteById(id);

        imageService.deleteImage(CLASS_FOLDER, id); // deletes the image associated with the language
        userSession.disNumLanguage(); // decrements the number of languages for the user session

        return "deleted_language";
    }
    // Display courses by language
    @GetMapping("/language/{id}/courses")
    public String showCoursesForLanguage(Model model, @PathVariable long id) {
        Class class1 = classService.findById(id);

        //404
        if (class1 == null) {
            return "page404"; // O cualquier otra lógica de manejo de error
        }

        model.addAttribute("class", class1); // adds the language attribute to the model
        return "show_courses_for_language";
    }



}
