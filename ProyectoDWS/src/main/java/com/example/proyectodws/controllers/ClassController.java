package com.example.proyectodws.controllers;

import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ClassController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/")
    public String home(Model model) {

        // Obtener los cursos destacados
        List<CourseDTO> featuredCourses = courseService.getFeaturedCourses();

        model.addAttribute("featuredCourses", featuredCourses);

        return "index";
    }

    @GetMapping("/about-us")
    public String aboutUs() {
        return "about_us";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}