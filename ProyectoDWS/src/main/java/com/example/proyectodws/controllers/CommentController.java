package com.example.proyectodws.controllers;

import java.sql.Date;
import java.time.LocalDate;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
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

        // Use jsoup to clean the input values.
        text = Jsoup.clean(text, "", Safelist.basic().addTags("p", "br", "strong", "em", "u", "s", "blockquote", "ol", "ul", "li", "h1", "h2", "h3"));

        if (text == null || text.trim().isEmpty()) {
            return "errorScreens/error400";
        }

        CourseDTO course = courseService.getCourseById(id);
        if (course != null) {
            UserDTO defaultUser = userService.getLoggedUserDTO();

            CommentDTO comment = new CommentDTO(null, text, Date.valueOf(LocalDate.now()));
            commentService.addComment(defaultUser.id(), id, comment);

            return "redirect:/course/{id}";
        }

        return "errorScreens/error404";
    }

    @GetMapping("/course/{courseId}/comments/{commentId}/edit")
    public String editComment(@PathVariable Long courseId, @PathVariable Long commentId, Model model) {
        CommentDTO comment = commentService.getCommentById(commentId);
        CourseDTO course = courseService.getCourseById(courseId);

        if (course == null) {
            return "errorScreens/error404";
        }

        if (comment == null) {
            return "errorScreens/error404";
        }

        UserDTO commentUser = commentService.getUserFromCommentId(commentId);
        if (commentUser == null) {
            return "errorScreens/error404";
        }

        UserDTO currentUser = userService.getLoggedUserDTO();
        if (!currentUser.roles().contains("ADMIN") && !commentUser.id().equals(currentUser.id())) {
            return "errorScreens/error403";
        }

        model.addAttribute("course", course);
        model.addAttribute("comment", comment);
        return "comments/edit_comment";
    }

    @PostMapping("/course/{courseId}/comments/{commentId}/update")
    public String updateComment(@PathVariable Long courseId, @PathVariable Long commentId, @RequestParam String text, Model model) {

        // Use jsoup to clean the input values.
        text = Jsoup.clean(text, "", Safelist.basic().addTags("p", "br", "strong", "em", "u", "s", "blockquote", "ol", "ul", "li", "h1", "h2", "h3"));

        if (text == null || text.trim().isEmpty()) {
            return "errorScreens/error400";
        }

        UserDTO commentUser = commentService.getUserFromCommentId(commentId);
        if (commentUser == null) {
            return "errorScreens/error404";
        }

        UserDTO currentUser = userService.getLoggedUserDTO();
        if (!currentUser.roles().contains("ADMIN") && !commentUser.id().equals(currentUser.id())) {
            return "errorScreens/error403";
        }

        commentService.updateComment(commentId, new CommentDTO(commentId, text, null));

        model.addAttribute("courseId", courseId);
        model.addAttribute("comment", commentService.getCommentById(commentId));
        return "comments/edited_comment";
    }

    @PostMapping("/course/{courseId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long courseId, @PathVariable Long commentId) {

        UserDTO commentUser = commentService.getUserFromCommentId(commentId);
        if (commentUser == null) {
            return "errorScreens/error404";
        }

        UserDTO currentUser = userService.getLoggedUserDTO();
        if (!currentUser.roles().contains("ADMIN") && !commentUser.id().equals(currentUser.id())) {
            return "errorScreens/error403";
        }
        commentService.deleteComment(commentId);
        return "redirect:/course/{courseId}";
    }
}