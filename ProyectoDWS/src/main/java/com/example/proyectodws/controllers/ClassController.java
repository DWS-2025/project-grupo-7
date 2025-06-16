package com.example.proyectodws.controllers;

import com.example.proyectodws.entities.Course;
import com.example.proyectodws.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

//this code is for subjects
@Controller
public class ClassController {

    @Autowired
    private CourseService courseService;

    // Return all courses
    @GetMapping("/")
    public String home(Model model) {

        // Obtener los cursos destacados
        List<Course> featuredCourses = courseService.getFeaturedCourses();

        model.addAttribute("featuredCourses", featuredCourses);

        return "index";
    }

    @GetMapping("/aboutUS")
    public String aboutUs() {
        return "about_us";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/ds")
    public String ds() {
        return "ds";
    }

}
