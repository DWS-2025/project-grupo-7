package com.example.proyectodws.service;

import com.example.proyectodws.dto.CommentDTO;
import com.example.proyectodws.dto.CommentMapper;
import com.example.proyectodws.dto.CommentWithIdsDTO;
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

// Service for comments.
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

    @Autowired
    private CommentMapper commentMapper;

    // add comment
    public CommentDTO addComment(Long userId, Long courseId, CommentDTO commentDTO) {
        Comment comment = new Comment();

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        comment.setText(commentDTO.text());
        comment.setUser(user);
        comment.setCourse(course);
        comment.setCreatedAt(commentDTO.createdAt());

        user.addComment(comment);
        course.addComment(comment);

        userRepository.save(user);
        courseRepository.save(course);

        return commentMapper.toDTO(comment);
    }

    // Get comments for course
    public List<CommentWithRelationsDTO> getCommentsForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElse(null);
        if (course == null) {
            return null;
        }

        List<Comment> comments = course.getComments();

        List<CommentWithRelationsDTO> commentDTOs = new ArrayList<>();
        comments.forEach(comment -> {
            commentDTOs.add(convertCommentToDTO(comment));
        });
        return commentDTOs;
    }

    // Get comments for course without related information.
    public List<CommentWithIdsDTO> getCommentsForCourseCompact(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElse(null);
        if (course == null) {
            return null;
        }

        List<Comment> comments = course.getComments();
        List<CommentWithIdsDTO> commentDTOs = new ArrayList<>();
        comments.forEach(comment -> {
            commentDTOs.add(new CommentWithIdsDTO(comment.getId(), comment.getText(), comment.getUser().getId(), comment.getCourse().getId(), comment.getCreatedAt()));
        });
        return commentDTOs;
    }

    // get comment by id
    public CommentDTO getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElse(null);
        if (comment == null) {
            return null;
        }
        return commentMapper.toDTO(comment);
    }

    // get user from comment id
    public UserDTO getUserFromCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElse(null);
        if (comment == null) {
            return null;
        }
        return userMapper.toDTO(comment.getUser());
    }

    // delete comment
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    // delete user comments
    public void deleteUserComments(Long userId) {
        List<Comment> comments = commentRepository.findByUserId(userId);
        comments.forEach(comment -> {
            commentRepository.delete(comment);
        });
    }

    // convert comment to dto with relations
    public CommentWithRelationsDTO convertCommentToDTO(Comment comment) {

        UserDTO userDTO = userMapper.toDTO(comment.getUser());
        CourseDTO courseDTO = courseMapper.toDTO(comment.getCourse());

        return new CommentWithRelationsDTO(comment.getId(), comment.getText(), userDTO, courseDTO, comment.getCreatedAt());
    }
}
