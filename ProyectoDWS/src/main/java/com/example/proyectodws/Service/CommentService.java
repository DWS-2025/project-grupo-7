package com.example.proyectodws.Service;
import java.util.Optional;

import com.example.proyectodws.Entities.Comment;
import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.User;
import com.example.proyectodws.Repository.CommentRepository;
import com.example.proyectodws.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSession userSession;

    public Optional<Comment> findById(long id) {
        return commentsRepository.findById(id);
    }

    public void save(Course courseToComment, Comment comment) {
        courseToComment.getComments().add(comment);
        User currentUser = userService.getUserById(0L);
        comment.setAuthor(currentUser);
        currentUser.getComments().add(comment);
        commentsRepository.save(comment);
    }

    public void delete(Long commentId, Course course) {
        // We assume that the comment exists
        Comment comment = this.findById(commentId).get();
        course.getComments().remove(comment);
        User author = comment.getAuthor();
        author.getComments().remove(comment);
        commentsRepository.delete(comment);
    }
}