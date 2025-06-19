package com.example.proyectodws.controllers;

import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.NewCourseRequestDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.dto.SubjectDTO;
import com.example.proyectodws.dto.UpdateCourseRequestDTO;
import com.example.proyectodws.entities.Course;
import com.example.proyectodws.service.*;

import jakarta.servlet.http.HttpSession;
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

// Controller for managing courses.
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

    //Display all courses
    @GetMapping("/courses")
    public String showCourses(Model model, HttpSession session) {
        model.addAttribute("courses", courseService.getAllCourses()); // add the list to the model
        return "courses";
    }

    // Display the manage courses form.
    @GetMapping("/courses/manage")
    public String manageCourses(Model model, HttpSession session) {
        model.addAttribute("courses", courseService.getAllCourses()); // add the list to the model
        return "courses/manage_form";
    }

    // Display the new course form with available subjects.
    @GetMapping("/courses/new")
    public String newCourseForm(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("course", new Course());
        return "courses/new_course";
    }

    // Create a new course.
    @PostMapping("/courses/saved")
    public String createCourse(Model model, @ModelAttribute NewCourseRequestDTO newCourseRequest) throws IOException, SQLException {
        try {
            // Get subjects from IDs and add them to course
            List<SubjectDTO> subjects = newCourseRequest.subjects().stream()
                    .map(id -> subjectService.getSubjectById(id))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            CourseDTO course = new CourseDTO(
                    null,
                    newCourseRequest.title(),
                    newCourseRequest.description(),
                    newCourseRequest.image().getOriginalFilename(),
                    false,
                    subjects,
                    newCourseRequest.video().getOriginalFilename()
            );

            MultipartFile image = newCourseRequest.image();
            MultipartFile video = newCourseRequest.video();
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

    // Display specific course by id.
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

    // Download the image of a course.
    @GetMapping("/course/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        Resource course = courseService.getCourseImage(id); // Supposing that `getLanguageById` returns an object `Post`
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(course);
    }

    // Display edit course form
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

    // Save edited course
    @PostMapping("/course/{id}/edited_course")
    public String editCourse (Model model, @PathVariable long id, UpdateCourseRequestDTO updatedCourse) {

        List<SubjectDTO> subjects = updatedCourse.subjects().stream()
                .map(subjectId -> subjectService.getSubjectById(subjectId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        CourseDTO updatedCourseDTO = new CourseDTO(
                updatedCourse.id(),
                updatedCourse.title(),
                updatedCourse.description(),
                null,
                false,
                subjects,
                null
        );

        courseService.updateCourse(id, updatedCourseDTO);
        model.addAttribute("course", updatedCourseDTO);
        return "courses/edited_course";
    }

    // Enroll in a course
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

    // Unenroll from a course
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

    // Display enrolled courses for the user logged in.
    @GetMapping("/enrolled_courses")
    public String showEnrolledCourses(Model model) {

        UserDTO user = userService.getLoggedUserDTO();

        model.addAttribute("enrolledCourses", userService.getEnrolledCourses(user));
        return "courses/my_courses";
    }

    // Display enrolled students in a determinated course
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

    // Add a subject to a course.
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
            // Delete course
            courseService.deleteCourse(id);
            userSession.disNumCourses(); // Decrease the number of courses for the user session

            // Remove the course from the user's course list if it is associated
            for (UserDTO user : userService.getAllUsers()) {
                if (user.courses().contains(course)) {
                    user.courses().remove(course);
                    userService.saveUser(user); // Save changes to the user
                }
            }
        }
        return "courses/deleted_course";
    }
}
