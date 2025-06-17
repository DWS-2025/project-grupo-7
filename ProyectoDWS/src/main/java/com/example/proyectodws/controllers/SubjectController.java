package com.example.proyectodws.controllers;

import com.example.proyectodws.entities.Subject;
import com.example.proyectodws.service.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
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
        Page<Subject> subjectPage = subjectService.getSubjects(PageRequest.of(0, 10));
        model.addAttribute("subjects", subjectPage.getContent());
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", subjectPage.getTotalPages());
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
    public String createSubject(Model model, @ModelAttribute @Validated Subject subject, BindingResult result,
                                @RequestParam("image") MultipartFile image) throws IOException, SQLException {

        if (subject.getTitle() == null || subject.getTitle().isEmpty()) {
            result.rejectValue("title", "error.title", "El título es obligatorio");
            return "subjects/new_subject";
        }

        String cleanedTitle = Jsoup.clean(subject.getTitle(), Safelist.simpleText().addTags("li", "ol", "ul"));
        subject.setTitle(cleanedTitle);

        if (subject.getText() != null) {
            String cleanedText = Jsoup.clean(subject.getText(), Safelist.simpleText().addTags("li", "ol", "ul"));
            subject.setText(cleanedText);
        }

        if (!image.isEmpty()) {
            subject.setImage(image.getOriginalFilename());
            subjectService.save(subject, image);
        } else {
            subjectService.createSubject(subject);
        }

        userSession.incNumSubjects();
        model.addAttribute("numPosts", userSession.getNumSubjects());

        return "subjects/saved_subject";
    }



    @GetMapping("/subject/{id}")
    public String showSubject(Model model, @PathVariable long id) {
        Subject subject = subjectService.getSubjectById(id);
        // if doesn't find the subject throws the error page
        if (subject == null) {
            return "errorScreens/error404";
        }
        // if finds the subject add to the model
        model.addAttribute("subject", subject);
        return "subjects/show_subject";
    }

    @GetMapping("/subject/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

        Subject subject = subjectService.getSubjectById(id); // Supposing that `getLanguageById` returns an object `Post`

        if (subject.getImageFile() != null) {
            Resource file = new InputStreamResource(subject.getImageFile().getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .contentLength(subject.getImageFile().length()).body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete subject
    @GetMapping("/subject/{id}/delete")
    public String deleteSubject(Model model, @PathVariable long id) throws IOException {
        Subject subject = subjectService.getSubjectById(id);

        if (subject == null) {
            return "errorScreens/error404";
        }

        subjectService.deleteSubject(id);
        imageService.deleteImage(SUBJECTS_FOLDER, id); // Delete the image associated with the language
        userSession.disNumSubject(); // Decrease the number of languages for the user session

        return "subjects/deleted_subject";
    }


    @GetMapping("/subject/{id}/courses")
    public String showCoursesForSubject(Model model, @PathVariable long id) {
        Subject subject = subjectService.getSubjectById(id);

        //404
        if (subject == null) {
            return "errorScreens/error404";
        }

        model.addAttribute("subject", subject); // adds the language attribute to the model
        return "courses/show_courses_for_subject";
    }

    @GetMapping("/subject/{id}/edit")
    public String editSubjectForm(Model model, @PathVariable long id) {
        Subject subject = subjectService.getSubjectById(id);
        if (subject == null) {
            return "errorScreens/error404";
        }
        model.addAttribute("subject", subject);
        return "subjects/edited_subject";
    }

    @PostMapping("/subject/{id}/edit")
    public String editSubject(@PathVariable long id, @ModelAttribute @Validated Subject subject, BindingResult result) {
        if (result.hasErrors()) {
            return "subjects/edited_subject";
        }

        Subject existingSubject = subjectService.getSubjectById(id);
        if (existingSubject == null) {
            return "errorScreens/error404";
        }

        existingSubject.setTitle(subject.getTitle());
        existingSubject.setText(subject.getText());


        subjectService.updateSubject(existingSubject);

        return "redirect:/subject/" + id;
    }

}

