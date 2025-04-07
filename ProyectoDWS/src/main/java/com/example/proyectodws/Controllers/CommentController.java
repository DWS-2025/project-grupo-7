package com.example.proyectodws.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommentController {

    @GetMapping("/comments")
    public String getCommentsPage() {
        return "comments";
    }
    }