package com.example.proyectodws.rest;

import com.example.proyectodws.dto.CommentDTO;
import com.example.proyectodws.dto.CommentWithRelationsDTO;
import com.example.proyectodws.entities.Comment;
import com.example.proyectodws.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> createComment(
            @RequestParam String text,
            @RequestParam Long userId,
            @RequestParam Long courseId
    ) {
        CommentDTO comment = new CommentDTO(
                null,
                text
        );

        return ResponseEntity.ok(commentService.addComment(userId, courseId, comment));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CommentWithRelationsDTO>> getCommentsByCourse(@PathVariable Long courseId) {
        List<CommentWithRelationsDTO> comments = commentService.getCommentsForCourse(courseId);
        return ResponseEntity.ok(comments.size() > 10 ? comments.subList(0, 10) : comments);
    }

    // delete comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}