package com.example.proyectodws.service;

import com.example.proyectodws.dto.CommentDTO;
import com.example.proyectodws.dto.CommentWithRelationsDTO;
import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.CourseMapper;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.dto.UserMapper;
import com.example.proyectodws.entities.Comment;
import com.example.proyectodws.entities.Course;
import com.example.proyectodws.entities.User;
import com.example.proyectodws.repository.CommentRepository;
import com.example.proyectodws.repository.CourseRepository;
import com.example.proyectodws.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseMapper courseMapper;

    // add comment
    public Comment addComment(Long userId, Long courseId, CommentDTO commentDTO) {
        Comment comment = new Comment();

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        comment.setText(commentDTO.text());
        comment.setUser(user);
        comment.setCourse(course);

        user.addComment(comment);
        course.addComment(comment);

        userRepository.save(user);
        courseRepository.save(course);

        return comment;
    }

    // get comments
    public List<CommentWithRelationsDTO> getCommentsForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        List<Comment> comments = course.getComments();

        List<CommentWithRelationsDTO> commentDTOs = new ArrayList<>();
        comments.forEach(comment -> {
            commentDTOs.add(convertCommentToDTO(comment));
        });
        return commentDTOs;
    }

    // delete comment
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public CommentWithRelationsDTO convertCommentToDTO(Comment comment) {

        UserDTO userDTO = userMapper.toDTO(comment.getUser());
        CourseDTO courseDTO = courseMapper.toDTO(comment.getCourse());

        return new CommentWithRelationsDTO(comment.getId(), comment.getText(), userDTO, courseDTO);
    }
}
