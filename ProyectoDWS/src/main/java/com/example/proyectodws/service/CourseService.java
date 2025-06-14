package com.example.proyectodws.service;

import com.example.proyectodws.entities.Course;
import com.example.proyectodws.entities.User;
import com.example.proyectodws.repository.CourseRepository;
import com.example.proyectodws.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.sql.rowset.serial.SerialBlob;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;


    // Look for and return all courses
    public Course save(Course course, MultipartFile image) throws IOException, SQLException {
        if (image != null && !image.isEmpty()) {
            byte[] imageBytes = image.getBytes();
            Blob blob = new SerialBlob(imageBytes);
            course.setImageFile(blob);
        }

        return courseRepository.save(course);
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    /*public Course getCourseById(Long id){
        Optional<Course> optionalCourse = courseRepository.findById(id);
        return optionalCourse.orElse(null);
    }*/
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
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

    public List<Course> getFeaturedCourses() {
        return courseRepository.findByIsFeaturedTrue();
    }
}

