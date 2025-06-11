package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.Subject;
import com.example.proyectodws.Entities.User;
import com.example.proyectodws.Repository.CourseRepository;
import com.example.proyectodws.Repository.SubjectRepository;
import com.example.proyectodws.Repository.UserRepository;

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
        // Crear algunos subjects (asignaturas)
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
        // Guardar los subjects
        subjectRepository.saveAll(Arrays.asList(subject1, subject2));

        // Crear algunos users (usuarios)
        User user1 = new User("John", "Doe", "johndoe", "profile1.png", "1234");
        User user2 = new User("Jane", "Doe", "janedoe", "profile2.png", "1234");

        // Guardar los usuarios
        userRepository.saveAll(Arrays.asList(user1, user2));

        // Crear algunos cursos (courses)
        Course course1 = new Course(subject1, "Algebra", "Introduction to algebra.");
        Course course2 = new Course(subject2, "Java Programming", "Learn the basics of Java.");

        // Guardar los cursos
        courseRepository.saveAll(Arrays.asList(course1, course2));

        // Asociar usuarios a los cursos (inscribir estudiantes)
        course1.getEnrolledStudents().add(user1);  // John Doe se inscribe en el curso de Algebra
        course1.getEnrolledStudents().add(user2);  // Jane Doe se inscribe en el curso de Algebra

        course2.getEnrolledStudents().add(user2);  // Jane Doe se inscribe en el curso de Java Programming

        // Guardar los cambios en los cursos
        courseRepository.saveAll(Arrays.asList(course1, course2));
    }
}

