package com.example.proyectodws.Controllers;

import com.example.proyectodws.Entities.Comment;
import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.Subject;
import com.example.proyectodws.Entities.User;
import com.example.proyectodws.Repository.CourseRepository;
import com.example.proyectodws.Repository.SubjectRepository;
import com.example.proyectodws.Repository.UserRepository;
import com.example.proyectodws.Service.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    private ClassService classService;

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
        return "manage_form";
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
        return "new_course";
    }


    // Save new course
        /*@PostMapping("/courses/saved")
        public String newCourse(Model model, @RequestParam("subjectId") Long subjectId,
                                @RequestParam("title") String title,
                                @RequestParam("description") String description) {

            Course course = new Course();
            course.setTitle(title);
            course.setDescription(description);

            Subject subject = subjectService.getSubjectById(subjectId);
            course.setSubject(subject);

            courseService.createCourse(course);
            userSession.incNumCourses();

            if (subject != null) {
                subject.getAssociatedCourses().add(course);
                subjectService.createSubject(subject);
            }

            model.addAttribute("numCourses", userSession.getNumCourses());
            return "saved_course";
        }*/
       /* @PostMapping("/courses/saved")
        public String newCourse(@Validated Course course, Model model) {
            courseService.createCourse(course);
            userSession.incNumCourses();
            model.addAttribute("numCourses", userSession.getNumCourses());
            return "saved_course";
        }*/



    // Display course with ID
    @GetMapping("/course/{id}")
    public String showCourse(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        model.addAttribute("comments", commentService.getCommentsForCourse(id));
        return "show_course";
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
        return "deleted_course";
    }


    // Display edit course form
    @GetMapping("/course/{id}/edit")
    public String editCourseForm(Model model, @PathVariable long id) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course); // adds the edit course to the model
        return "edit_course";
    }

    // Save edited course
    @PostMapping("/course/{id}/edited_course")
    public String editCourse (Model model, @PathVariable long id, Course updatedCourse) {
        Course existingCourse = courseService.getCourseById(id);
        existingCourse.setTitle(updatedCourse.getTitle()); // updates the title
        existingCourse.setDescription(updatedCourse.getDescription()); // updates description
        courseService.createCourse(existingCourse); // saves updated course

        model.addAttribute("course", existingCourse);
        return "edited_course";
    }

    // Enroll in a course
    @PostMapping("/course/{id}/enroll")
    public String enrollInCourse(Model model, @PathVariable long id) {
        Course course = courseService.getCourseById(id);
        User user = new User();
        user.setUsername("Equipo de administración");
        userSession.enrollInCourse(course,user); // enrolls the user in the course
        return "enrolled_courses";
    }

    // Display enrolled students
    @GetMapping("/enrolled_courses")
    public String showEnrolledCourses(Model model) {
        model.addAttribute("enrolledCourses", userSession.getEnrolledCourses());
        return "my_courses";
    }

    // Display enrolled students in a determinated course
    @GetMapping("/course/{id}/enrolledStudents")
    public String showEnrolledStudents(Model model, @PathVariable long id) {
        Course course = courseService.getCourseById(id);

        if (course == null) {
            return "Error404";
        }

        model.addAttribute("course", course);
        model.addAttribute("enrolledStudents", course.getEnrolledStudents());
        return "enrolled_students";
    }
    @GetMapping("/courses/search")
    public String searchCoursesForm() {
        return "search_courses";
    }

    @GetMapping("/courses/results")
    public String searchCoursesByTitles(@RequestParam("subjectTitle") String subjectTitle, @RequestParam("courseTitle") String courseTitle, Model model) {
        List<Course> courses = courseService.findCoursesByTitles(subjectTitle, courseTitle);
        model.addAttribute("courses", courses);
        return "search_results";
    }


    @PostMapping("/course/{id}/comments/new")
    public String addComment(@PathVariable Long id, @RequestParam String text, Model model) {

        Course course = courseService.getCourseById(id);
        if (course != null) {

            User defaultUser = userRepository.findByUsername("defaultUser")
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));;


            if (defaultUser == null) {

                defaultUser = new User(
                        "defaultUser", // username
                        "Juan",        // firstName
                        "Pérez",       // lastName
                        "default.jpg",  // imageName,
                        "1234"
                );


                userRepository.save(defaultUser);
            }


            Comment comment = new Comment(text, defaultUser, course);
            commentService.addComment(comment);


            return "redirect:/course/{id}";
        }


        return "errorPage";
    }




    @PostMapping("/course/{courseId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long courseId, @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/course/{courseId}";
    }
       /* @GetMapping("/courses/{id}")
        public String getCourse(@PathVariable Long id, Model model) {
            Course course = courseRepository.findById(id).orElseThrow();
            List<Subject> allSubjects = subjectRepository.findAll();
            model.addAttribute("course", course);
            model.addAttribute("allSubjects", allSubjects);
            model.addAttribute("subject", new Subject()); // para formulario
            return "course_detail";
        }*/

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
    @PostMapping("/courses/saved")
    public String newCourse(@Validated Course course, Model model) {

        Long subjectId = course.getSubject().getId();


        Subject selectedSubject = subjectService.getSubjectById(subjectId);

        if (selectedSubject != null) {
            course.setSubject(selectedSubject);
        }


        courseService.createCourse(course);


        userSession.incNumCourses();
        model.addAttribute("numCourses", userSession.getNumCourses());

        return "saved_course";
    }
    @GetMapping("/course/{id}/image")
    public ResponseEntity<byte[]> getCourseImage(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        if (course != null && course.getSubject() != null && course.getSubject().getImageFile() != null) {
            Blob imageBlob = course.getSubject().getImageFile();
            try {
                byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imageBytes);
            } catch (SQLException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
