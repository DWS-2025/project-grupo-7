package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Comment;
import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.User;
import com.example.proyectodws.Repository.CommentRepository;
import com.example.proyectodws.Repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Agregar un nuevo comentario
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // Obtener todos los comentarios de un curso
    public List<Comment> getCommentsForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        return course.getComments();
    }

    // Eliminar un comentario
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
