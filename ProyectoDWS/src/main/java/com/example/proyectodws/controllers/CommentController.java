package com.example.proyectodws.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.proyectodws.entities.Comment;
import com.example.proyectodws.entities.Course;
import com.example.proyectodws.entities.User;
import com.example.proyectodws.repository.UserRepository;
import com.example.proyectodws.service.CommentService;
import com.example.proyectodws.service.CourseService;

@Controller
public class CommentController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/comments")
    public String getCommentsPage() {
        return "comments";
    }

    @PostMapping("/course/{id}/comments/new")
    public String addComment(@PathVariable Long id, @RequestParam String text, Model model) {

        Course course = courseService.getCourseById(id);
        if (course != null) {
            // TODO: Change to the user logged in
            User defaultUser = userRepository.findByUsername("johndoe")
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Comment comment = new Comment(text, defaultUser, course);
            commentService.addComment(comment);

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
