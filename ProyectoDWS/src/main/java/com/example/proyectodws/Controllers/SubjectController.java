package com.example.proyectodws.Controllers;

import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.Subject;
import com.example.proyectodws.Service.*;
import jakarta.servlet.http.HttpSession;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class SubjectController {

    private static final String SUBJECTS_FOLDER = "subjects";

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserSession userSession;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/subjects")
    public String showSubjects(Model model, HttpSession session) {
        List<Subject> allSubjects = subjectService.getAllSubjects();
        System.out.println("Número de asignaturas encontradas: " + allSubjects.size());
        for (Subject s : allSubjects) {
            System.out.println("ID: " + s.getId() + " | Título: " + s.getTitle());
        }

        model.addAttribute("subjects", allSubjects);
        model.addAttribute("welcome", session.isNew());
        return "subjects";
    }


  /* @GetMapping("/subjects/new")
   public String newPostForm(Model model) {

       model.addAttribute("user", userSession.getUser()); // add the user attribute to the model

       return "new_subject";
   }*/

    /*@GetMapping("/subjects/new")
    public String newPostForm(Model model) {
        model.addAttribute("user", userSession.getUser());
        model.addAttribute("subject", new Subject());
        return "new_subject";
    }

    @PostMapping("/subject/new")
    public String newSubject(Model model, @Validated Subject subject, BindingResult result, MultipartFile image) throws IOException, SQLException {

        if (subject.getTitle() == null || subject.getTitle().isEmpty()) {
            result.rejectValue("title", "error.title", "El título es obligatorio");
            return "new_subject";
        }else{
            String cleanedTitle = Jsoup.clean(subject.getTitle(), Safelist.simpleText().addTags("li", "ol","ul"));
            subject.setTitle(cleanedTitle);
        }

        if (subject.getText()!=null){
            String cleanedText = Jsoup.clean(subject.getText(), Safelist.simpleText().addTags("li","ol","ul"));
            subject.setText(cleanedText);
        }

        subjectService.createSubject(subject);
        subject.setImage(image.getOriginalFilename());
        subjectService.save(subject, image);
        userSession.incNumSubjects();
        model.addAttribute("numPosts", userSession.getNumSubjects());

        return "saved_subject";
    }*/
    @GetMapping("/subjects/new")
    public String newSubjectForm(Model model) {
        model.addAttribute("subject", new Subject());
        model.addAttribute("user", userSession.getUser());
        return "new_subject";
    }

    @PostMapping("/subjects/new")
    public String createSubject(Model model, @ModelAttribute @Validated Subject subject, BindingResult result,
                                @RequestParam("image") MultipartFile image) throws IOException, SQLException {

        if (subject.getTitle() == null || subject.getTitle().isEmpty()) {
            result.rejectValue("title", "error.title", "El título es obligatorio");
            return "new_subject";
        }

        String cleanedTitle = Jsoup.clean(subject.getTitle(), Safelist.simpleText().addTags("li", "ol", "ul"));
        subject.setTitle(cleanedTitle);

        if (subject.getText() != null) {
            String cleanedText = Jsoup.clean(subject.getText(), Safelist.simpleText().addTags("li", "ol", "ul"));
            subject.setText(cleanedText);
        }

        if (!image.isEmpty()) {
            subject.setImage(image.getOriginalFilename());
            subjectService.save(subject, image);  // << Asegúrate de que este método hace el save + image
        } else {
            subjectService.createSubject(subject);
        }

        userSession.incNumSubjects();
        model.addAttribute("numPosts", userSession.getNumSubjects());

        return "saved_subject";
    }



    @GetMapping("/subject/{id}")
    public String showSubject(Model model, @PathVariable long id) {
        Subject subject = subjectService.getSubjectById(id);
        // if doesn't find the subject throws the error page
        if (subject == null) {
            return "Error404";
        }
        // if finds the subject add to the model
        model.addAttribute("subject", subject);
        return "show_subject";
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
            for (Course course : subject.getAssociatedCourses()) {
                courseService.deleteCourse(course.getId());
            }
        }

        subjectService.deleteSubject(id);
        imageService.deleteImage(SUBJECTS_FOLDER, id); // Delete the image associated with the language
        userSession.disNumSubject(); // Decrease the number of languages for the user session

        return "deleted_subject";
    }


    @GetMapping("/subject/{id}/courses")
    public String showCoursesForSubject(Model model, @PathVariable long id) {
        Subject subject = subjectService.getSubjectById(id);

        //404
        if (subject == null) {
            return "page404";
        }

        model.addAttribute("subject", subject); // adds the language attribute to the model
        return "show_courses_for_subject";
    }
    @GetMapping("/subject/{id}/edit")
    public String editSubjectForm(Model model, @PathVariable long id) {
        Subject subject = subjectService.getSubjectById(id);
        if (subject == null) {
            return "Error404";
        }
        model.addAttribute("subject", subject);
        return "edited_subject";
    }
    @PostMapping("/subject/{id}/edit")
    public String editSubject(@PathVariable long id, @ModelAttribute @Validated Subject subject, BindingResult result) {
        if (result.hasErrors()) {
            return "edited_subject";
        }

        Subject existingSubject = subjectService.getSubjectById(id);
        if (existingSubject == null) {
            return "Error404"; //
        }

        existingSubject.setTitle(subject.getTitle());
        existingSubject.setText(subject.getText());


        subjectService.updateSubject(existingSubject);

        return "redirect:/subject/" + id;
    }




}

