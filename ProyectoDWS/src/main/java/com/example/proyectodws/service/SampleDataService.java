package com.example.proyectodws.service;

import com.example.proyectodws.entities.Course;
import com.example.proyectodws.entities.Subject;
import com.example.proyectodws.entities.User;
import com.example.proyectodws.repository.CourseRepository;
import com.example.proyectodws.repository.SubjectRepository;
import com.example.proyectodws.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Service for creating sample data.
@Service
public class SampleDataService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MediaService mediaService;
    @Autowired
    private SubjectRepository subjectRepository;

    // This method will be executed when the application starts.
    @PostConstruct
    public void init() throws SQLException, IOException {
        // Check if subjects already exist
        if (subjectRepository.findAll().isEmpty()) {

            // Create some subjects
            Subject subject1 = new Subject("Matemáticas", "Curso sobre conceptos fundamentales de matemáticas, álgebra y cálculo.");
            Subject subject2 = new Subject("Programación", "Introducción a la programación y desarrollo de software.");
            Subject subject3 = new Subject("Física", "Estudio de las leyes fundamentales del universo y la materia.");
            Subject subject4 = new Subject("Química", "Exploración de la composición y propiedades de la materia.");
            Subject subject5 = new Subject("Biología", "Estudio de los seres vivos y sus procesos vitales.");
            Subject subject6 = new Subject("Historia", "Análisis de eventos y civilizaciones a través del tiempo.");
            Subject subject7 = new Subject("Literatura", "Estudio de obras literarias y técnicas de escritura.");
            Subject subject8 = new Subject("Inglés", "Aprendizaje del idioma inglés y su cultura.");
            Subject subject9 = new Subject("Francés", "Introducción al idioma francés y su cultura.");
            Subject subject10 = new Subject("Alemán", "Bases del idioma alemán y aspectos culturales.");
            Subject subject11 = new Subject("Economía", "Principios básicos de economía y finanzas.");
            Subject subject12 = new Subject("Filosofía", "Exploración del pensamiento y razonamiento filosófico.");
            Subject subject13 = new Subject("Arte", "Historia del arte y técnicas artísticas.");
            Subject subject14 = new Subject("Música", "Teoría musical y apreciación musical.");
            Subject subject15 = new Subject("Geografía", "Estudio de la Tierra y sus características.");
            Subject subject16 = new Subject("Psicología", "Introducción a la mente y el comportamiento humano.");
            Subject subject17 = new Subject("Sociología", "Estudio de la sociedad y las relaciones humanas.");
            Subject subject18 = new Subject("Estadística", "Análisis y interpretación de datos.");
            Subject subject19 = new Subject("Tecnología", "Avances tecnológicos y su impacto en la sociedad.");
            Subject subject20 = new Subject("Educación Física", "Desarrollo físico y deportivo.");

            // Create some images for the subjects
            try {
                Blob mathImage = mediaService.filePathToBlob("images\\matematicas.jpg");
                subject1.setImageFile(mathImage);
                subject1.setImage("matematicas.jpg");
            } catch (IOException e) {
                System.err.println("Image matematicas.jpg not found or unreadable");
            }

            try {
                Blob csImage = mediaService.filePathToBlob("images\\images.jpeg");
                subject2.setImageFile(csImage);
                subject2.setImage("images.jpeg");
            } catch (IOException e) {
                System.err.println("Image images.jpeg not found or unreadable");
            }

            // Save the subjects
            subjectRepository.saveAll(Arrays.asList(subject1, subject2, subject3, subject4, subject5, subject6, subject7, subject8, subject9, subject10, subject11, subject12, subject13, subject14, subject15, subject16, subject17, subject18, subject19, subject20));


            // Set roles for users
            List<String> user1Roles = new ArrayList<>(Arrays.asList("USER", "ADMIN"));
            List<String> user2Roles = new ArrayList<>(Arrays.asList("USER"));

            // Create some users
            User user1 = new User("John", "Doe", "johndoe", new BCryptPasswordEncoder().encode("1234"), user1Roles);
            User user2 = new User("Jane", "Doe", "janedoe", new BCryptPasswordEncoder().encode("1234"), user2Roles);

            // Check if users already exist
            if (userRepository.findAll().isEmpty()) {

                // Create images for users
                try {
                    // Convert profile1.png to MultipartFile and save it
                    Blob profile1Blob = mediaService.filePathToBlob("images\\profile1.png");
                    user1.setImageFile(profile1Blob);
                    user1.setImage("profile1.png");

                    // Convert profile2.png to MultipartFile and save it
                    Blob profile2Blob = mediaService.filePathToBlob("images\\profile2.png");
                    user2.setImageFile(profile2Blob);
                    user2.setImage("profile2.png");
                } catch (IOException e) {
                    System.err.println("Error saving user profile images");
                }

                // Save the users
                userRepository.saveAll(Arrays.asList(user1, user2));

                // Check if courses already exist
                if (courseRepository.findAll().isEmpty()) {
                    // Create some courses
                    Course course1 = new Course("Algebra", "Introduction to algebra.", true, "/videos/course_sample.mp4");
                    Course course2 = new Course("Java Programming", "Learn the basics of Java.", true, "/videos/course_sample.mp4");

                    course1.addSubject(subject1);
                    course1.addSubject(subject2);
                    course2.addSubject(subject1);
                    course2.addSubject(subject2);

                    // Create some images for the courses
                    try {
                        Blob course1Image = mediaService.filePathToBlob("images\\matematicas.jpg");
                        course1.setImageFile(course1Image);
                        course1.setImage("matematicas.jpg");
                    } catch (IOException e) {
                        System.err.println("Image matematicas.jpg not found or unreadable");
                    }

                    try {
                        Blob course2Image = mediaService.filePathToBlob("images\\images.jpeg");
                        course2.setImageFile(course2Image);
                        course2.setImage("images.jpeg");
                    } catch (IOException e) {
                        System.err.println("Image images.jpeg not found or unreadable");
                    }

                    // Save the courses
                    courseRepository.saveAll(Arrays.asList(course1, course2));

                    // Associate users to the courses (enroll students)
                    course1.getEnrolledStudents().add(user1);  // John Doe enrolls in the Algebra course
                    course1.getEnrolledStudents().add(user2);  // Jane Doe enrolls in the Algebra course

                    course2.getEnrolledStudents().add(user2);  // Jane Doe enrolls in the Java Programming course

                    // Save the changes in the courses
                    courseRepository.saveAll(Arrays.asList(course1, course2));
                }
            }
        }
    }
}


