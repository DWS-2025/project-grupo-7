package com.example.proyectodws.Controllers;

import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.Subject;
import com.example.proyectodws.Service.CourseService;
import com.example.proyectodws.Service.ClassService;
import com.example.proyectodws.Service.SubjectService;
import com.example.proyectodws.Service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ClassService classService;

    //Display all courses
    @GetMapping("/courses")
    public String showCourses(Model model, HttpSession session) {
        model.addAttribute("courses", courseService.findAll()); // add the list to the model
        model.addAttribute("welcome", session.isNew()); // 'welcome' to indicates new session
        return "courses";
    }
    @GetMapping("/courses/manage")
    public String manageCourses(Model model, HttpSession session) {
        model.addAttribute("courses", courseService.findAll()); // add the list to the model
        model.addAttribute("welcome", session.isNew()); // 'welcome' for new session
        return "manage_form";
    }

    // Display form for new course
    @GetMapping("/course/new")
    public String newCourseForm(Model model) {
        Object user = userService.getUser();

        if (user == null) {
            return "errorScreens/Error404";
        }

        model.addAttribute("user", user);
        return "new_course";
    }

    // Save new course
    @PostMapping("/courses/saved")
    public String newCourse(Model model, Course course) {
        courseService.save(course);
        userService.incNumCourses(); // increments number of courses for user
        Subject matchingClass = classService.getPostBySubject(course.getSubject());

        // if some language matches add new course
        if (matchingClass != null) {
            matchingClass.getAssociatedCourses().add(course);
        }
        model.addAttribute("numCourses", userService.getNumCourses()); // adds the number of courses to the model
        return "saved_course";
    }

    // Display course with ID
    @GetMapping("/course/{id}")
    public String showCourse(Model model, @PathVariable long id) {
        Course course = courseService.findById(id);
        // if doesn't find the course throws the error page
        if (course == null) {
            return "Error404";
        }
        // if finds the course add to the model
        model.addAttribute("course", course);
        return "show_course";
    }

    // Delete course
    @GetMapping("/course/{id}/delete")
    public String deleteCourse(Model model, @PathVariable long id) {
        Course course = courseService.findById(id);

        if (course == null) {
            return "Error404"; // Si el curso no existe, devuelve página de error
        }

        // delete course of the list of courses of user
        userService.removeCourseFromUsers(course);

        // delete course of bbdd
        courseService.deleteById(id);

        // number of courses of users down
        userService.disNumCourses();

        return "deleted_course";
    }


    // Display edit course form
    @GetMapping("/course/{id}/edit")
    public String editCourseForm(Model model, @PathVariable long id) {
        Course course = courseService.findById(id);
        model.addAttribute("course", course); // adds the edit course to the model
        return "edit_course";
    }

    // Save edited course
    @PostMapping("/course/{id}/edited_course")
    public String editCourse (Model model, @PathVariable long id, Course updatedCourse) {
        Course existingCourse = courseService.findById(id);
        existingCourse.setTitle(updatedCourse.getTitle()); // updates the title
        existingCourse.setDescription(updatedCourse.getDescription()); // updates description
        courseService.save(existingCourse); // saves updated course

        model.addAttribute("course", existingCourse);
        return "edited_course";
    }

    // Enroll in a course
    @PostMapping("/course/{id}/enroll")
    public String enrollInCourse(Model model, @PathVariable long id) {
        Course course = courseService.findById(id);
        userService.enrollInCourse(course); // enrolls the user in the course
        return "enrolled_courses";
    }

    // Display enrolled students
    @GetMapping("/enrolled_courses")
    public String showEnrolledCourses(Model model) {
        model.addAttribute("enrolledCourses", userService.getEnrolledCourses());
        return "my_courses";
    }

    // Display enrolled students in a determinated course
    @GetMapping("/course/{id}/enrolledStudents")
    public String showEnrolledStudents(Model model, @PathVariable long id) {
        Course course = courseService.findById(id);

        if (course == null) {
            return "Error404";
        }

        model.addAttribute("course", course);
        model.addAttribute("enrolledStudents", course.getEnrolledStudents()); // Agrega la lista de estudiantes matriculados al modelo
        return "enrolled_students";
    }

}
