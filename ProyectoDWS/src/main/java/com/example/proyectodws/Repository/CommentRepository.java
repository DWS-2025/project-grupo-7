package com.example.proyectodws.Repository;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.example.proyectodws.Entities.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentRepository {


    private AtomicLong nextId = new AtomicLong(1L);
    private ConcurrentHashMap<Long, Comment> comments = new ConcurrentHashMap<>();

    public Optional<Comment> findById(long id) {
        return Optional.ofNullable(comments.get(id));
    }

    public void save(Comment comment) {
        long id = comment.getId();
        if (id == 0) {
            id = nextId.getAndIncrement();
            comment.setId(id);
        }
        comments.put(id, comment);
    }

    public void delete(Comment comment) {
        comments.remove(comment.getId());
    }
}
