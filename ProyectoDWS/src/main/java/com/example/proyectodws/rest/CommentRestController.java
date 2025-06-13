package com.example.proyectodws.rest;

import com.example.proyectodws.entities.Comment;
import com.example.proyectodws.entities.Course;
import com.example.proyectodws.entities.User;
import com.example.proyectodws.service.CommentService;
import com.example.proyectodws.service.CourseService;
import com.example.proyectodws.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity<Comment> createComment(
            @RequestParam String text,
            @RequestParam Long userId,
            @RequestParam Long courseId
    ) {
        User user = userService.getUserById(userId);
        Course course = courseService.getCourseById(courseId);

        if (user == null || course == null) return ResponseEntity.badRequest().build();

        Comment comment = new Comment(text, user, course);
        return ResponseEntity.ok(commentService.addComment(comment));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Comment>> getCommentsByCourse(@PathVariable Long courseId) {
        List<Comment> comments = commentService.getCommentsForCourse(courseId);
        return ResponseEntity.ok(comments.size() > 10 ? comments.subList(0, 10) : comments);
    }

    // delete comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}