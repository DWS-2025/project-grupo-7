package com.example.proyectodws.Entities;

import jakarta.persistence.*;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String message;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;  // Relación con el curso

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;  // Relación con el autor del comentario

    protected Comment() {
    }

    public Comment(String message) {
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Comment [id=" + id + ", message=" + message + "]";
    }
}
