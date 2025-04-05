package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Comment;
import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.Subject;
import com.example.proyectodws.Entities.User;
import com.example.proyectodws.Repository.CommentRepository;
import com.example.proyectodws.Repository.CourseRepository;
import com.example.proyectodws.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SampleDataService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentsRepository;

    @PostConstruct
    public void init() {

        // Default user
        User equipo = new User("Equipo", "Todos","michel.maes@urjc.es", "TextoImagen");

        // Other users
        User prueba = new User("Prueba", "prueba","carlos@urjc.es", "textoImagen");

        // Some examples of posts and comments
        Subject Math= new Subject("Math", "Text");

        Course course = new Course(Math, "Avanzado", "Es guay");

        Comment comment = new Comment("Cool!");
        comment.setAuthor(equipo);
        equipo.getComments().add(comment);
        course.getComments().add(comment);
        commentsRepository.save(comment);

        Comment comment2 = new Comment("I like it!");
        comment2.setAuthor(prueba);
        prueba.getComments().add(comment2);
        course.getComments().add(comment2);
        commentsRepository.save(comment2);

        courseRepository.save(course);
        userRepository.save(equipo);
        userRepository.save(prueba);
    }

}
