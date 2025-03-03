package com.example.proyectodws.Controllers;

import com.example.proyectodws.Entities.Subject;
import com.example.proyectodws.Service.CourseService;
import com.example.proyectodws.Service.ImageService;
import com.example.proyectodws.Service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class SubjectController {
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ImageService imageService;

    @GetMapping("/subjects")
    public String listSubjects(Model model) {
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "subjects";
    }

    @GetMapping("/subjects/new")
    public String newSubject(Model model) {
        model.addAttribute("subject", new Subject());
        return "new_subject";
    }

    @PostMapping("/subjects/new")
    public String saveSubject(@ModelAttribute Subject subject) {
        subjectService.saveSubject(subject);
        return "redirect:/subjects";
    }

    @GetMapping("/subjects/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Subject subject = subjectService.getSubjectById(id);
        model.addAttribute("subject", subject);
        return "edit_subject";
    }

    @PostMapping("/subjects/update/{id}")
    public String updateSubject(@PathVariable Long id, @ModelAttribute Subject subject) {
        subjectService.updateSubject(id, subject);
        return "redirect:/subjects";
    }

    @GetMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return "redirect:/subjects";
    }
}

