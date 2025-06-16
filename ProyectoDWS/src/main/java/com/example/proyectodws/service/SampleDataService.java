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
        // Crear algunos subjects (asignaturas)
        // Check if subjects already exist
        if (subjectRepository.findAll().isEmpty()) {
            Subject subject1 = new Subject("Matemáticas", "Un curso sobre matemáticas.");
            Subject subject2 = new Subject("Programación", "Un curso sobre programación.");

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

            // Check if users already exist
            if (userRepository.findAll().isEmpty()) {
                // Crear algunos users (usuarios)
                User user1 = new User("John", "Doe", "defaultUser", "profile1.png", "1234");
                User user2 = new User("Jane", "Doe", "janedoe", "profile2.png", "1234");

                // Guardar los usuarios
                userRepository.saveAll(Arrays.asList(user1, user2));

                // Check if courses already exist
                if (courseRepository.findAll().isEmpty()) {
                    // Crear algunos cursos (courses)
                    Course course1 = new Course("Algebra", "Introduction to algebra.", true);
                    Course course2 = new Course("Java Programming", "Learn the basics of Java.", true);

                    course1.addSubject(subject1);
                    course1.addSubject(subject2);
                    course2.addSubject(subject1);
                    course2.addSubject(subject2);

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
        }
    }
}
