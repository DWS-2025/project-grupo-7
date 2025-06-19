package com.example.proyectodws.controllers;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.proyectodws.dto.CommentDTO;
import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.service.CommentService;
import com.example.proyectodws.service.CourseService;
import com.example.proyectodws.service.UserService;

// Controller for managing comments.
@Controller
public class CommentController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    // Get comments page
    @GetMapping("/comments")
    public String getCommentsPage() {
        return "comments";
    }

    // Add a new comment
    @PostMapping("/course/{id}/comments/new")
    public String addComment(@PathVariable Long id, @RequestParam String text, Model model) {

        CourseDTO course = courseService.getCourseById(id);
        if (course != null) {
            UserDTO defaultUser = userService.getLoggedUserDTO();

            CommentDTO comment = new CommentDTO(null, text, Date.valueOf(LocalDate.now()));
            commentService.addComment(defaultUser.id(), id, comment);

            return "redirect:/course/{id}";
        }

        return "errorScreens/error404";
    }

    // Delete a comment
    @PostMapping("/course/{courseId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long courseId, @PathVariable Long commentId) {

        UserDTO commentUser = commentService.getUserFromCommentId(commentId);
        if (commentUser == null) {
            return "errorScreens/error404";
        }

        UserDTO currentUser = userService.getLoggedUserDTO();
        if (!commentUser.id().equals(currentUser.id())) {
            return "errorScreens/error403";
        }

        commentService.deleteComment(commentId);
        return "redirect:/course/{courseId}";
    }
}
