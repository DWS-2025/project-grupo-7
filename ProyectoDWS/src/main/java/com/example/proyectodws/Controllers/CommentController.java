package com.example.proyectodws.Controllers;

import com.example.proyectodws.Entities.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CommentController {

    // List to store comments
    private List<Comment> comments =new ArrayList<>();

    // Add the list of comments to the model and returns the name of the template
    @GetMapping("/comments")
    public String showComments(Model model) {
        model.addAttribute("comments", comments);
        return "comments";
    }

    // Creates new object, add the object to the list and redirects the user back to "/comments"
    @PostMapping("/comments/add")
    public String addComment(@RequestParam String username, @RequestParam String comment) {
        Comment newComment = new Comment(username, comment);
        comments.add(newComment);
        return "redirect:/comments";
    }

}