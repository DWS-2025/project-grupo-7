package com.example.proyectodws.controllers;

import com.example.proyectodws.entities.Comment;
import com.example.proyectodws.entities.Course;
import com.example.proyectodws.entities.Subject;
import com.example.proyectodws.entities.User;
import com.example.proyectodws.repository.CourseRepository;
import com.example.proyectodws.repository.SubjectRepository;
import com.example.proyectodws.repository.UserRepository;
import com.example.proyectodws.service.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private UserRepository userRepository;
    //Display all courses
    @GetMapping("/courses")
    public String showCourses(Model model, HttpSession session) {
        model.addAttribute("courses", courseService.getAllCourses()); // add the list to the model
        model.addAttribute("welcome", session.isNew()); // 'welcome' to indicates new session
        return "courses";
    }
    @GetMapping("/courses/manage")
    public String manageCourses(Model model, HttpSession session) {
        model.addAttribute("courses", courseService.getAllCourses()); // add the list to the model
        model.addAttribute("welcome", session.isNew()); // 'welcome' for new session
        return "courses/manage_form";
    }

    // Display form for new course
    /* @GetMapping("/course/new")
    public String newCourseForm(Model model) {
        Collection<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        model.addAttribute("user", userSession.getUser());
        return "new_course";
    }*/
    @GetMapping("/courses/new")
    public String newCourseForm(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("course", new Course());
        return "courses/new_course";
    }

    @PostMapping("/courses/saved")
    public String createSubject(Model model, @ModelAttribute @Validated Course course, BindingResult result,
                                @RequestParam("image") MultipartFile image,
                                @RequestParam("subjects") List<Long> subjectIds) throws IOException, SQLException {
        try {
            // Get subjects from IDs and add them to course
            List<Subject> subjects = subjectIds.stream()
                    .map(id -> subjectRepository.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            course.setSubjects(subjects);

            if (!image.isEmpty()) {
                course.setImage(image.getOriginalFilename());
                courseService.save(course, image);
            } else {
                courseService.createCourse(course);
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

    // Display course with ID
    @GetMapping("/course/{id}")
    public String showCourse(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        model.addAttribute("comments", commentService.getCommentsForCourse(id));
        return "courses/show_course";
    }

    @GetMapping("/course/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

        Course course = courseService.getCourseById(id); // Supposing that `getLanguageById` returns an object `Post`

        if (course.getImageFile() != null) {
            Resource file = new InputStreamResource(course.getImageFile().getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .contentLength(course.getImageFile().length()).body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Delete course
    @GetMapping("/course/{id}/delete")
    public String deleteCourse(Model model, @PathVariable long id) {
        Course course = courseService.getCourseById(id);
        if (course != null) {
            // Delete course
            courseService.deleteCourse(id);
            userSession.disNumCourses(); // Decrease the number of courses for the user session

            // Remove the course from the user's course list if it is associated
            for (User user : userService.getAllUsers()) {
                if (user.getCourses().contains(course)) {
                    user.removeCourse(course);
                    userService.createUser(user); // Save changes to the user
                }
            }
        }
        return "courses/deleted_course";
    }


    // Display edit course form
    @GetMapping("/course/{id}/edit")
    public String editCourseForm(Model model, @PathVariable long id) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course); // adds the edit course to the model
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "courses/edit_course";
    }

    // Save edited course
    @PostMapping("/course/{id}/edited_course")
    public String editCourse (Model model, @PathVariable long id, Course updatedCourse, @RequestParam(required = false) MultipartFile image) {
        Course existingCourse = courseService.getCourseById(id);
        existingCourse.setTitle(updatedCourse.getTitle()); // updates the title
        existingCourse.setDescription(updatedCourse.getDescription()); // updates description
        existingCourse.setSubjects(updatedCourse.getSubjects()); // updates subjects

        try {
            courseService.save(existingCourse, image); // saves updated course
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("course", existingCourse);
        return "courses/edited_course";
    }

    // Enroll in a course
    @PostMapping("/course/{id}/enroll")
    public String enrollInCourse(Model model, @PathVariable long id) {
        Course course = courseService.getCourseById(id);
        User user = new User();
        user.setUsername("Equipo de administración");
        userSession.enrollInCourse(course,user); // enrolls the user in the course
        return "courses/enrolled_courses";
    }

    // Display enrolled students
    @GetMapping("/enrolled_courses")
    public String showEnrolledCourses(Model model) {
        model.addAttribute("enrolledCourses", userSession.getEnrolledCourses());
        return "courses/my_courses";
    }

    // Display enrolled students in a determinated course
    @GetMapping("/course/{id}/enrolledStudents")
    public String showEnrolledStudents(Model model, @PathVariable long id) {
        Course course = courseService.getCourseById(id);

        if (course == null) {
            return "Error404.html";
        }

        model.addAttribute("course", course);
        model.addAttribute("enrolledStudents", course.getEnrolledStudents());
        return "courses/enrolled_students";
    }
    @GetMapping("/courses/search")
    public String searchCoursesForm() {
        return "courses/search_courses";
    }

    @GetMapping("/courses/results")
    public String searchCoursesByTitles(@RequestParam("subjectTitle") String subjectTitle, @RequestParam("courseTitle") String courseTitle, Model model) {
        List<Course> courses = courseService.findCoursesByTitles(subjectTitle, courseTitle);
        model.addAttribute("courses", courses);
        return "courses/search_results";
    }

    @PostMapping("/courses/{id}/add-subject")
    public String addSubjectToCourse(@PathVariable Long id, @RequestParam Long subjectId) {
        Course course = courseRepository.findById(id).orElseThrow();
        Subject subject = subjectRepository.findById(subjectId).orElseThrow();

        if (!course.getSubjects().contains(subject)) {
            course.getSubjects().add(subject);
            courseRepository.save(course);
        }

        return "redirect:/courses/" + id;
    }
}

