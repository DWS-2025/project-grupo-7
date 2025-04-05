package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.User;
import com.example.proyectodws.Repository.CourseRepository;
import com.example.proyectodws.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;


    // Look for and return all courses
    public Course createCourse(Course course){
        return courseRepository.save(course);
    }

    public Course getCourseById(Long id){
        Optional<Course> optionalCourse = courseRepository.findById(id);
        return optionalCourse.orElse(null);
    }

    public List<Course> getAllCourses(){
        return courseRepository.findAll();
    }

    public void deleteCourse (Long id){
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();

            List<User> enrolledStudents = course.getEnrolledStudents();
            for (User user : enrolledStudents) {
                user.getCourses().remove(course);
                userRepository.save(user);
            }
            courseRepository.deleteById(id);
        }
    }

    public List<Course> findCoursesByTitles(String languageTitle, String courseTitle) {
        return courseRepository.findCoursesByTitles(languageTitle, courseTitle);
    }


}
