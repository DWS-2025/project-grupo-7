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
    private ImageService imageService;
    @Autowired
    private SubjectRepository subjectRepository;

    // Este método se ejecutará cuando la aplicación se inicie
    @PostConstruct
    public void init() throws SQLException, IOException {

        if (subjectRepository.findAll().isEmpty()) {
            Subject subject1 = new Subject("Matemáticas", "Veremos la asignatura de matemáticas.");
            Subject subject2 = new Subject("Programación", "Veremos la asignatura de programación.");
            Subject subject3 = new Subject("Lengua", "Veremos la asignatura de lengua.");
            Subject subject4 = new Subject("Historia", "Veremos la asignatura de historia.");
            Subject subject5 = new Subject("Geografia", "Veremos la asignatura de geografia.");
            Subject subject6 = new Subject("Musica", "Veremos la asignatura de musica.");
            Subject subject7 = new Subject("Ingenieria del Software", "Veremos la asignatura de IS.");
            Subject subject8 = new Subject("Estructuras de datos", "Veremos la asignatura de ED.");
            Subject subject9 = new Subject("Calculo", "Veremos la asignatura de calculo.");
            Subject subject10 = new Subject("Desarrollo de Web Seguro", "Veremos la asignatura de DWS.");
            Subject subject11 = new Subject("Conocimiento del medio", "Veremos la asignatura de conocimiento del medio.");
            Subject subject12 = new Subject("Educacion a la ciudadania", "Veremos la asignatura de EC.");

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
            try {
                Blob mathImage = imageService.filePathToBlob("images\\matematicas.jpg");
                subject3.setImageFile(mathImage);
                subject3.setImage("matematicas.jpg");
            } catch (IOException e) {
                System.err.println("Image matematicas.jpg not found or unreadable");
            }
            try {
                Blob mathImage = imageService.filePathToBlob("images\\matematicas.jpg");
                subject4.setImageFile(mathImage);
                subject4.setImage("matematicas.jpg");
            } catch (IOException e) {
                System.err.println("Image matematicas.jpg not found or unreadable");
            }
            try {
                Blob mathImage = imageService.filePathToBlob("images\\matematicas.jpg");
                subject5.setImageFile(mathImage);
                subject5.setImage("matematicas.jpg");
            } catch (IOException e) {
                System.err.println("Image matematicas.jpg not found or unreadable");
            }
            try {
                Blob mathImage = imageService.filePathToBlob("images\\matematicas.jpg");
                subject6.setImageFile(mathImage);
                subject6.setImage("matematicas.jpg");
            } catch (IOException e) {
                System.err.println("Image matematicas.jpg not found or unreadable");
            }
            try {
                Blob mathImage = imageService.filePathToBlob("images\\matematicas.jpg");
                subject7.setImageFile(mathImage);
                subject7.setImage("matematicas.jpg");
            } catch (IOException e) {
                System.err.println("Image matematicas.jpg not found or unreadable");
            }
            try {
                Blob mathImage = imageService.filePathToBlob("images\\matematicas.jpg");
                subject8.setImageFile(mathImage);
                subject8.setImage("matematicas.jpg");
            } catch (IOException e) {
                System.err.println("Image matematicas.jpg not found or unreadable");
            }
            try {
                Blob mathImage = imageService.filePathToBlob("images\\matematicas.jpg");
                subject9.setImageFile(mathImage);
                subject9.setImage("matematicas.jpg");
            } catch (IOException e) {
                System.err.println("Image matematicas.jpg not found or unreadable");
            }
            try {
                Blob mathImage = imageService.filePathToBlob("images\\matematicas.jpg");
                subject10.setImageFile(mathImage);
                subject10.setImage("matematicas.jpg");
            } catch (IOException e) {
                System.err.println("Image matematicas.jpg not found or unreadable");
            }
            try {
                Blob mathImage = imageService.filePathToBlob("images\\matematicas.jpg");
                subject11.setImageFile(mathImage);
                subject11.setImage("matematicas.jpg");
            } catch (IOException e) {
                System.err.println("Image matematicas.jpg not found or unreadable");
            }
            try {
                Blob mathImage = imageService.filePathToBlob("images\\matematicas.jpg");
                subject12.setImageFile(mathImage);
                subject12.setImage("matematicas.jpg");
            } catch (IOException e) {
                System.err.println("Image matematicas.jpg not found or unreadable");
            }

            // save subjects
            subjectRepository.saveAll(Arrays.asList(subject1, subject2, subject3, subject4, subject5, subject6, subject7, subject8,subject9, subject10, subject11, subject12));

            // Check if users already exist
            if (userRepository.findAll().isEmpty()) {

                // Create images for users
                String user1Image = null;
                String user2Image = null;
                try {
                    // Convert profile1.png to MultipartFile and save it
                    Blob profile1Blob = imageService.filePathToBlob("images\\profile1.png");
                    user1Image = imageService.saveImage(profile1Blob);

                    // Convert profile2.png to MultipartFile and save it
                    Blob profile2Blob = imageService.filePathToBlob("images\\profile2.png");
                    user2Image = imageService.saveImage(profile2Blob);
                } catch (IOException e) {
                    System.err.println("Error saving user profile images");
                }

                // Set roles for users
                List<String> user1Roles = new ArrayList<>(Arrays.asList("USER", "ADMIN"));
                List<String> user2Roles = new ArrayList<>(Arrays.asList("USER"));

                // Create some users
                User user1 = new User("John", "Doe", "johndoe", user1Image, "1234", user1Roles);
                User user2 = new User("Jane", "Doe", "janedoe", user2Image, "1234", user2Roles);

                // Save the users
                userRepository.saveAll(Arrays.asList(user1, user2));



                // Check if courses already exist
                if (courseRepository.findAll().isEmpty()) {
                    //create some courses
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

                    //save courses
                    courseRepository.saveAll(Arrays.asList(course1, course2));

                    //associated users-courses
                    course1.getEnrolledStudents().add(user1);  // John -> Algebra
                    course1.getEnrolledStudents().add(user2);  // Jane -> Algebra

                    course2.getEnrolledStudents().add(user2);  // Jane Doe -> Java Programming

                    //save changes in courses
                    courseRepository.saveAll(Arrays.asList(course1, course2));
                }
            }
        }
    }
}
