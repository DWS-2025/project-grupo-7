package com.example.proyectodws.service;

import com.example.proyectodws.entities.Course;
import com.example.proyectodws.entities.Subject;
import com.example.proyectodws.entities.User;
import com.example.proyectodws.repository.CourseRepository;
import com.example.proyectodws.repository.SubjectRepository;
import com.example.proyectodws.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

@Service
public class SampleDataService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ImageService imageService;
    @Autowired
    private SubjectRepository subjectRepository;

    // Este método se ejecutará cuando la aplicación se inicie
    @PostConstruct
    public void init() throws SQLException, IOException {
        // create subjects
        Subject subject1 = new Subject("Mathematics", "A subject about mathematics.");
        Subject subject2 = new Subject("Computer Science", "A subject about computer programming.");

        try {
            Blob mathImage = imageService.filePathToBlob("images\\matematicas.jpg");
            subject1.setImageFile(mathImage);
            subject1.setImage("matematicas.jpg");
        } catch (IOException e) {
            System.err.println("Image matematicas.jpg not found or unreadable");
        }

        try {
            Blob csImage = imageService.filePathToBlob("images\\images.jpeg");
            subject2.setImageFile(csImage);
            subject2.setImage("images.jpeg");
        } catch (IOException e) {
            System.err.println("Image images.jpeg not found or unreadable");
        }

        // save subjects
        subjectRepository.saveAll(Arrays.asList(subject1, subject2));

        // create users
        User user1 = new User("John", "Doe", "johndoe", "profile1.png", "1234");
        User user2 = new User("Jane", "Doe", "janedoe", "profile2.png", "1234");

        // save users
        userRepository.saveAll(Arrays.asList(user1, user2));

        // create courses
        Course course1 = new Course(subject1, "Algebra", "Introduction to algebra.", true);
        Course course2 = new Course(subject2, "Java Programming", "Learn the basics of Java.", true);

        try {
            Blob course1Image = imageService.filePathToBlob("images\\matematicas.jpg");
            course1.setImageFile(course1Image);
            course1.setImage("matematicas.jpg");
        } catch (IOException e) {
            System.err.println("Image matematicas.jpg not found or unreadable");
        }

        try {
            Blob course2Image = imageService.filePathToBlob("images\\images.jpeg");
            course2.setImageFile(course2Image);
            course2.setImage("images.jpeg");
        } catch (IOException e) {
            System.err.println("Image images.jpeg not found or unreadable");
        }

        // save courses
        courseRepository.saveAll(Arrays.asList(course1, course2));

        // users - courses
        course1.getEnrolledStudents().add(user1);  // John Doe se inscribe en el curso de Algebra
        course1.getEnrolledStudents().add(user2);  // Jane Doe se inscribe en el curso de Algebra

        course2.getEnrolledStudents().add(user2);  // Jane Doe se inscribe en el curso de Java Programming

        // save
        courseRepository.saveAll(Arrays.asList(course1, course2));
    }
}

