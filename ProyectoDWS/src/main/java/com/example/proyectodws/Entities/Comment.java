package com.example.proyectodws.Entities;

public class Comment {
    private long id;

    private String message;

    private User author;

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
