    package com.example.proyectodws.Controllers;

    import com.example.proyectodws.Entities.Comment;
    import com.example.proyectodws.Entities.Course;
    import com.example.proyectodws.Entities.Subject;
    import com.example.proyectodws.Entities.User;
    import com.example.proyectodws.Repository.CourseRepository;
    import com.example.proyectodws.Repository.SubjectRepository;
    import com.example.proyectodws.Service.*;

    import jakarta.servlet.http.HttpSession;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestParam;

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

        private CourseRepository courseRepository;
        private SubjectRepository subjectRepository;

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
        public String showCourse(Model model, @PathVariable long id) {
            Course course = courseService.getCourseById(id);
            System.out.println("¿Curso encontrado? " + (course != null ? "Sí: " + course.getTitle() : "No"));
            if (course == null) {
                return "Error404";
            }
            model.addAttribute("course", course);
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
            model.addAttribute("enrolledStudents", course.getEnrolledStudents()); // Agrega la lista de estudiantes matriculados al modelo
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
        public String newComment(@PathVariable long id, Comment comment) {
            Optional<Course> op = Optional.ofNullable(courseService.getCourseById(id));
            if (op.isPresent()) {
                Course course = op.get();
                //comment.setAuthor(equipo);
                commentService.save(course, comment);
                return "redirect:/course/" + id;
            } else {
                return "error404";
            }
        }

        @PostMapping("/course/{id}/comments/{commentId}/delete")
        public String deleteComment(@PathVariable Long id, @PathVariable Long commentId) {

            Optional<Course> op = Optional.ofNullable(courseService.getCourseById(id));

            if (op.isPresent()) {
                Course course = op.get();
                commentService.delete(commentId, course);
                return "redirect:/course/" + id;
            } else {
                return "error404";
            }

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
            courseService.createCourse(course);
            userSession.incNumCourses();
            model.addAttribute("numCourses", userSession.getNumCourses());
            return "saved_course";
        }



    }
