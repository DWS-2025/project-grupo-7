package com.example.proyectodws.controllers;

import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.NewCourseRequestDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.dto.SubjectDTO;
import com.example.proyectodws.dto.UpdateCourseRequestDTO;
import com.example.proyectodws.entities.Course;
import com.example.proyectodws.service.*;

import jakarta.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSession userSession;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/courses")
    public String showCourses(Model model, HttpSession session) {
        model.addAttribute("courses", courseService.getAllCourses()); // add the list to the model
        return "courses";
    }

    @GetMapping("/courses/manage")
    public String manageCourses(Model model, HttpSession session) {
        model.addAttribute("courses", courseService.getAllCourses()); // add the list to the model
        return "courses/manage_form";
    }

    @GetMapping("/courses/new")
    public String newCourseForm(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("course", new Course());
        return "courses/new_course";
    }

    @PostMapping("/courses/saved")
    public String createCourse(Model model, @ModelAttribute NewCourseRequestDTO newCourseRequest) throws IOException, SQLException {
        try {
            // Use jsoup to clean the input values.
            String title = Jsoup.clean(newCourseRequest.title(), "", Safelist.none());
            String description = Jsoup.clean(newCourseRequest.description(), "", Safelist.none());

            NewCourseRequestDTO newCourseRequestCleaned = new NewCourseRequestDTO(
                    title,
                    description,
                    newCourseRequest.image(),
                    newCourseRequest.subjects(),
                    newCourseRequest.video()
            );

            List<SubjectDTO> subjects = newCourseRequestCleaned.subjects().stream()
                    .map(id -> subjectService.getSubjectById(id))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            CourseDTO course = new CourseDTO(
                    null,
                    newCourseRequestCleaned.title(),
                    newCourseRequestCleaned.description(),
                    newCourseRequestCleaned.image().getOriginalFilename(),
                    false,
                    subjects,
                    newCourseRequestCleaned.video().getOriginalFilename()
            );

            MultipartFile image = newCourseRequestCleaned.image();
            MultipartFile video = newCourseRequestCleaned.video();
            if (!image.isEmpty()) {
                courseService.createWithMedia(course, image, video);
            }
            else {
                courseService.saveCourse(course);
            }

            model.addAttribute("course", course);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error creating course: " + e.getMessage());
            return "courses/new_course";
        }

        userSession.incNumCourses();
        model.addAttribute("numCourses", userSession.getNumCourses());

        return "courses/saved_course";
    }

    @GetMapping("/course/{id}")
    public String showCourse(@PathVariable Long id, Model model) {
        CourseDTO course = courseService.getCourseById(id);
        List<UserDTO> enrolledStudents = courseService.getEnrolledStudents(id);
        UserDTO user = userService.getLoggedUserDTO();

        model.addAttribute("course", course);
        model.addAttribute("enrolledStudents", enrolledStudents);
        model.addAttribute("comments", commentService.getCommentsForCourse(id));
        model.addAttribute("user", user);

        return "courses/show_course";
    }

    @GetMapping("/course/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        Resource course = courseService.getCourseImage(id); // Supposing that `getLanguageById` returns an object `Post`
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(course);
    }

    @GetMapping("/course/{id}/edit")
    public String editCourseForm(Model model, @PathVariable long id) {
        CourseDTO course = courseService.getCourseById(id);

        UpdateCourseRequestDTO updateCourseRequestDTO = new UpdateCourseRequestDTO(
                course.id(),
                course.title(),
                course.description(),
                course.subjects().stream().map(SubjectDTO::id).collect(Collectors.toList())
        );

        model.addAttribute("course", updateCourseRequestDTO); // adds the edit course to the model
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "courses/edit_course";
    }

    @PostMapping("/course/{id}/edited_course")
    public String editCourse (Model model, @PathVariable long id, UpdateCourseRequestDTO updatedCourse) {

        // Use jsoup to clean the input values.
        String title = Jsoup.clean(updatedCourse.title(), "", Safelist.none());
        String description = Jsoup.clean(updatedCourse.description(), "", Safelist.none());

        UpdateCourseRequestDTO updatedCourseCleaned = new UpdateCourseRequestDTO(
                updatedCourse.id(),
                title,
                description,
                updatedCourse.subjects()
        );

        List<SubjectDTO> subjects = updatedCourseCleaned.subjects().stream()
                .map(subjectId -> subjectService.getSubjectById(subjectId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        CourseDTO updatedCourseDTO = new CourseDTO(
                updatedCourseCleaned.id(),
                updatedCourseCleaned.title(),
                updatedCourseCleaned.description(),
                null,
                false,
                subjects,
                null
        );

        courseService.updateCourse(id, updatedCourseDTO);
        model.addAttribute("course", updatedCourseDTO);
        return "courses/edited_course";
    }

    @PostMapping("/course/{id}/enroll")
    public String enrollInCourse(Model model, @PathVariable long id) {
        CourseDTO course = courseService.getCourseById(id);

        if (course == null) {
            return "errorScreens/error404.html";
        }

        UserDTO user = userService.getLoggedUserDTO();

        if (!user.courses().contains(course)) {
            userService.enrollUserInCourse(user.id(), course.id());
        }
        else {
            return "courses/already_enrolled";
        }

        return "courses/enrolled_courses";
    }

    @PostMapping("/course/{id}/unenroll")
    public String unenrollFromCourse(Model model, @PathVariable long id) {
        CourseDTO course = courseService.getCourseById(id);
        UserDTO user = userService.getLoggedUserDTO();

        if (user.courses().contains(course)) {
            userService.unenrollUserFromCourse(user.id(), course.id());
            return "courses/unenrolled_courses";
        }
        else {
            return "courses/not_enrolled";
        }
    }

    @GetMapping("/enrolled_courses")
    public String showEnrolledCourses(Model model) {

        UserDTO user = userService.getLoggedUserDTO();

        model.addAttribute("enrolledCourses", userService.getEnrolledCourses(user));
        return "courses/my_courses";
    }

    @GetMapping("/course/{id}/enrolledStudents")
    public String showEnrolledStudents(Model model, @PathVariable long id) {
        CourseDTO course = courseService.getCourseById(id);

        if (course == null) {
            return "errorScreens/error404.html";
        }

        List<UserDTO> enrolledStudents = courseService.getEnrolledStudents(id);

        model.addAttribute("course", course);
        model.addAttribute("enrolledStudents", enrolledStudents);
        return "courses/enrolled_students";
    }

    @PostMapping("/courses/{id}/add-subject")
    public String addSubjectToCourse(@PathVariable Long id, @RequestParam Long subjectId) {
        CourseDTO course = courseService.getCourseById(id);
        SubjectDTO subject = subjectService.getSubjectById(subjectId);

        if (!course.subjects().contains(subject)) {
            course.subjects().add(subject);
            courseService.saveCourse(course);
        }

        return "redirect:/courses/" + id;
    }

    // Delete course
    @PostMapping("/course/{id}/delete")
    public String deleteCourse(Model model, @PathVariable long id) {
        CourseDTO course = courseService.getCourseById(id);
        if (course != null) {
            courseService.deleteCourse(id);
            userSession.disNumCourses(); // Decrease the number of courses for the user session

            for (UserDTO user : userService.getAllUsers()) {
                if (user.courses().contains(course)) {
                    user.courses().remove(course);

                    UserDTO userRequestDTO = new UserDTO(
                            user.id(),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            user.courses()
                    );

                    userService.updateUser(user.id(), userRequestDTO); // Save changes to the user
                }
            }
        }
        return "courses/deleted_course";
    }
}
