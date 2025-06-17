package com.example.proyectodws.controllers;

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

@Controller
public class CommentController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @GetMapping("/comments")
    public String getCommentsPage() {
        return "comments";
    }

    @PostMapping("/course/{id}/comments/new")
    public String addComment(@PathVariable Long id, @RequestParam String text, Model model) {

        CourseDTO course = courseService.getCourseById(id);
        if (course != null) {
            // TODO: Change to the user logged in
            UserDTO defaultUser = userService.getUserByUsername("johndoe");

            CommentDTO comment = new CommentDTO(null, text);
            commentService.addComment(defaultUser.id(), id, comment);

            return "redirect:/course/{id}";
        }


        return "errorPage";
    }




    @PostMapping("/course/{courseId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long courseId, @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/course/{courseId}";
    }
}