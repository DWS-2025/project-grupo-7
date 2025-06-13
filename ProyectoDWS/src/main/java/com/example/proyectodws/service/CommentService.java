package com.example.proyectodws.service;

import com.example.proyectodws.entities.Comment;
import com.example.proyectodws.entities.Course;
import com.example.proyectodws.repository.CommentRepository;
import com.example.proyectodws.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CourseRepository courseRepository;

    // add comment
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // get comments
    public List<Comment> getCommentsForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        return course.getComments();
    }

    // delete comment
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
