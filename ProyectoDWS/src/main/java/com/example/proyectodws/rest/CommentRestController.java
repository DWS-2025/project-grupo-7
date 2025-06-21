package com.example.proyectodws.rest;

import com.example.proyectodws.api.CommentResponse;
import com.example.proyectodws.api.CommentsResponse;
import com.example.proyectodws.api.GenericResponse;
import com.example.proyectodws.dto.CommentDTO;
import com.example.proyectodws.dto.CommentWithIdsDTO;
import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.service.CommentService;
import com.example.proyectodws.service.CourseService;
import com.example.proyectodws.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<CommentsResponse> getCommentsByCourse(@PathVariable Long courseId) {
        List<CommentWithIdsDTO> comments = commentService.getCommentsForCourseCompact(courseId);

        if (comments == null) {
            return ResponseEntity.status(404).body(new CommentsResponse(new GenericResponse("Curso no encontrado", 404), null));
        }

        if (comments.size() > 10) {
            comments = comments.subList(0, 10);
        }

        return ResponseEntity.ok(new CommentsResponse(new GenericResponse("Comentarios obtenidos correctamente", 200), comments));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @RequestParam Long courseId,
            @RequestParam String text
    ) {
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.status(400).body(new CommentResponse(new GenericResponse("El texto es obligatorio", 400), null));
        }
        if (courseId == null || courseId < 0) {
            return ResponseEntity.status(400).body(new CommentResponse(new GenericResponse("El curso es obligatorio", 400), null));
        }
        CourseDTO course = courseService.getCourseById(courseId);
        if (course == null) {
            return ResponseEntity.status(404).body(new CommentResponse(new GenericResponse("Curso no encontrado", 404), null));
        }

        UserDTO user = userService.getLoggedUserDTO();

        CommentDTO comment = new CommentDTO(
                null,
                text,
                Date.valueOf(LocalDate.now())
        );

        return ResponseEntity.ok(new CommentResponse(new GenericResponse("Comentario creado correctamente", 200), commentService.addComment(user.id(), courseId, comment)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<GenericResponse> deleteComment(@PathVariable Long commentId) {

        CommentDTO comment = commentService.getCommentById(commentId);
        if (comment == null) {
            return ResponseEntity.status(404).body(new GenericResponse("Comentario no encontrado", 404));
        }

        UserDTO commentUser = commentService.getUserFromCommentId(commentId);
        if (commentUser == null) {
            return ResponseEntity.status(404).body(new GenericResponse("Comentario no encontrado", 404));
        }

        UserDTO currentUser = userService.getLoggedUserDTO();
        if (!currentUser.roles().contains("ADMIN") && !commentUser.id().equals(currentUser.id())) {
            return ResponseEntity.status(403).body(new GenericResponse("No tienes permisos para eliminar este comentario", 403));
        }


        commentService.deleteComment(commentId);
        return ResponseEntity.ok(new GenericResponse("Comentario eliminado correctamente", 200));
    }
}
