package com.example.proyectodws.Controllers;

import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.Subject;
import com.example.proyectodws.Service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class SubjectController {

    private static final String SUBEJCTS_FOLDER = "subjects";

    @Autowired
    private SubjectService subjectService;
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserSession userSession;
    @Autowired
    private ClassService classService;

    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;

    @GetMapping("/subjects")
    public String showSubjects(Model model, HttpSession session) {
        model.addAttribute("subjects", subjectService.findAll()); // add the list to the model
        model.addAttribute("welcome", session.isNew()); // 'welcome' to indicates new session
        return "subjects";
    }

    @GetMapping("/subjects/manage")
    public String manageSubject(Model model, HttpSession session) {
        model.addAttribute("subjects", subjectService.findAll()); // add the list to the model
        model.addAttribute("welcome", session.isNew()); // 'welcome' for new session
        return "manage_form_subject";
    }

   @GetMapping("/subjects/new")
   public String newSubjectForm(Model model) {
       Object user = userService.getUser();

       if (user == null) {
           return "errorScreens/Error404";
       }

       model.addAttribute("user", user);
       return "new_subject";
   }

   @PostMapping("/subjects/saved")
   public String newSubject (@RequestParam String title, @RequestParam String text,
                              @RequestParam("image") MultipartFile image) throws IOException {

        Subject subject = new Subject();
        subject.setTitle(title);
        subject.setText(text);

        subjectService.saveSubject(subject);
        userService.incNumSubjects(); // increments number of subjects for user
       Subject matchingClass = classService.getPostBySubject(subject.getTitle());

        if (!image.isEmpty()) {
            imageService.saveImage("subjects", subject.getId(), image);
        }

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

    @GetMapping("/subject/{id}/edit")
    public String editSubjectForm(Model model, @PathVariable long id) {
        Subject subject = subjectService.getSubjectById(id);
        model.addAttribute("subject", subject);
        return "edit_subject";
    }

    // Save edited subject
    @PostMapping("/subject/{id}/edited_subject")
    public String editSubject (Model model, @PathVariable long id, Subject updatedSubject) {
        Subject existingSubject = subjectService.getSubjectById(id);
        existingSubject.setTitle(updatedSubject.getTitle()); // updates the title
        existingSubject.setText(updatedSubject.getText()); // updates text
        subjectService.saveSubject(existingSubject); // saves updated subject

        model.addAttribute("subject", existingSubject);
        return "edited_subject";
    }

    // Delete subject
    @GetMapping("/subject/{id}/delete")
    public String deleteSubject(Model model, @PathVariable long id) {
        Subject subject = subjectService.getSubjectById(id);

        if (subject == null) {
            return "Error404"; //
        }

        subjectService.deleteSubject(id);
        userService.disNumSubjects();

        return "deleted_subject";
    }
    // Enroll in a subject
    @PostMapping("/subject/{id}/enroll")
    public String enrollInSubject(Model model, @PathVariable long id) {
        Subject subject = subjectService.getSubjectById(id);
        userService.enrollInSubject(subject); // enrolls the user in the subject
        return "enrolled_subject";
    }
    // Display enrolled students
    @GetMapping("/enrolled_subject")
    public String showEnrolledSubjects(Model model) {
        model.addAttribute("enrolledSubjects", userService.getEnrolledSubjects());
        return "my_subjects";
    }

    @GetMapping("/subject/{id}/courses")
    public String showSubjectCourses(Model model, @PathVariable long id) {
        Subject subject = subjectService.getSubjectById(id);

        if (subject == null) {
            return "error404";
        }

        List<Course> subjectCourses = courseService.findAll().stream()
                .filter(course -> course.getSubject().equals(subject.getTitle()))
                .toList();

        model.addAttribute("subject", subject);
        model.addAttribute("courses", subjectCourses);

        return "subject_courses";
    }
    @GetMapping("/subject/{id}/image")
    public ResponseEntity<Object> getSubjectImage(@PathVariable long id) {
        return imageService.createResponseFromImage("subjects", id);
    }


}

