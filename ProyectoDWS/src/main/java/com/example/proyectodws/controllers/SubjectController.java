package com.example.proyectodws.controllers;

import com.example.proyectodws.dto.SubjectDTO;
import com.example.proyectodws.entities.Subject;
import com.example.proyectodws.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Controller
public class SubjectController {

    private static final String SUBJECTS_FOLDER = "subjects";

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserSession userSession;

    @Autowired
    private ImageService imageService;

    @GetMapping("/subjects")
    public String getSubjects(Model model) {
        List<SubjectDTO> subjects = subjectService.getSubjects(PageRequest.of(0, 10));
        model.addAttribute("subjects", subjects);
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", (int) Math.ceil(subjectService.getAllSubjects().size() / 10.0));
        model.addAttribute("subjectsPerPage", 10);
        return "subjects";
    }

    @GetMapping("/subjects/new")
    public String newSubjectForm(Model model) {
        model.addAttribute("subject", new Subject());
        model.addAttribute("user", userSession.getUser());
        return "subjects/new_subject";
    }

    @PostMapping("/subjects/new")
    public String createSubject(Model model, @ModelAttribute @Validated SubjectDTO subject, BindingResult result,
                                @RequestParam("image") MultipartFile image) throws IOException, SQLException {

        if (image != null && !image.isEmpty()) {
            subjectService.createWithImage(subject, image);
        }
        else {
            subjectService.saveSubject(subject);
        }

        return "subjects/saved_subject";
    }

    @GetMapping("/subject/{id}")
    public String showSubject(Model model, @PathVariable long id) {
        SubjectDTO subject = subjectService.getSubjectById(id);
        if (subject == null) {
            return "errorScreens/error404";
        }
        model.addAttribute("subject", subject);
        return "subjects/show_subject";
    }

    @GetMapping("/subject/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        Resource subject = subjectService.getSubjectImage(id);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(subject);
    }

    @GetMapping("/subject/{id}/courses")
    public String showCoursesForSubject(Model model, @PathVariable long id) {
        SubjectDTO subject = subjectService.getSubjectById(id);

        //404
        if (subject == null) {
            return "errorScreens/error404";
        }

        model.addAttribute("subject", subject);
        return "courses/show_courses_for_subject";
    }

    @GetMapping("/subject/{id}/edit")
    public String editSubjectForm(Model model, @PathVariable long id) {
        SubjectDTO subject = subjectService.getSubjectById(id);
        if (subject == null) {
            return "errorScreens/error404";
        }
        model.addAttribute("subject", subject);
        return "subjects/edited_subject";
    }

    @PostMapping("/subject/{id}/edit")
    public String editSubject(@PathVariable long id, @ModelAttribute @Validated SubjectDTO subject, BindingResult result) {
        if (result.hasErrors()) {
            return "subjects/edited_subject";
        }

        subjectService.updateSubject(id, subject);

        return "redirect:/subject/" + id;
    }

    @PostMapping("/subject/{id}/delete")
    public String deleteSubject(Model model, @PathVariable long id) throws IOException {
        SubjectDTO subject = subjectService.getSubjectById(id);

        if (subject == null) {
            return "errorScreens/error404";
        }

        subjectService.deleteSubject(id);
        imageService.deleteImage(SUBJECTS_FOLDER, id);
        userSession.disNumSubject();

        return "subjects/deleted_subject";
    }

}


