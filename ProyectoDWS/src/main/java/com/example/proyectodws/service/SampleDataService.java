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

    @PostConstruct
    public void init() throws SQLException, IOException {
        if (subjectRepository.findAll().isEmpty()) {

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

            subjectRepository.saveAll(Arrays.asList(subject1, subject2, subject3, subject4, subject5, subject6, subject7, subject8, subject9, subject10, subject11, subject12, subject13, subject14, subject15, subject16, subject17, subject18, subject19, subject20));

            if (userRepository.findAll().isEmpty()) {

                //create images for users
                String user1Image = null;
                String user2Image = null;
                try {
                    //convert profile1.png to MultipartFile and save it
                    Blob profile1Blob = mediaService.filePathToBlob("images\\profile1.png");
                    user1Image = mediaService.saveImage(profile1Blob);

                    //convert profile2.png to MultipartFile and save it
                    Blob profile2Blob = mediaService.filePathToBlob("images\\profile2.png");
                    user2Image = mediaService.saveImage(profile2Blob);
                } catch (IOException e) {
                    System.err.println("Error saving user profile images");
                }

                List<String> user1Roles = new ArrayList<>(Arrays.asList("USER", "ADMIN"));
                List<String> user2Roles = new ArrayList<>(Arrays.asList("USER"));

                User user1 = new User("John", "Doe", "johndoe", user1Image, new BCryptPasswordEncoder().encode("1234"), user1Roles);
                User user2 = new User("Jane", "Doe", "janedoe", user2Image, new BCryptPasswordEncoder().encode("1234"), user2Roles);

                userRepository.saveAll(Arrays.asList(user1, user2));

                //check if courses already exist
                if (courseRepository.findAll().isEmpty()) {
                    Course course1 = new Course("Algebra", "Introduction to algebra.", true, "/videos/course_sample.mp4");
                    Course course2 = new Course("Java Programming", "Learn the basics of Java.", true, "/videos/course_sample.mp4");

                    course1.addSubject(subject1);
                    course1.addSubject(subject2);
                    course2.addSubject(subject1);
                    course2.addSubject(subject2);

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

                    courseRepository.saveAll(Arrays.asList(course1, course2));

                    //associate users to the courses (enroll students)
                    course1.getEnrolledStudents().add(user1); //john-->algebra
                    course1.getEnrolledStudents().add(user2);  //jane-->algebra

                    course2.getEnrolledStudents().add(user2);  //jane-->java programming

                    courseRepository.saveAll(Arrays.asList(course1, course2));
                }
            }
        }
    }
}
