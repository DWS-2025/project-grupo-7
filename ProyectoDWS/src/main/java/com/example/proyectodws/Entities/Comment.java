package com.example.proyectodws.Entities;

import jakarta.persistence.*;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String text;
    @ManyToOne(cascade = CascadeType.PERSIST)  // Esto asegura que el usuario sea guardado automáticamente
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;  // Relación con el curso


    public Comment(String text, User author, Course course) {
        this.text = text;
        this.author = author;
        this.course = course;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }
    public Comment() {}

    public void setText(String text) {
        this.text = text;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }


    @Override
    public String toString() {
        return "Comment [id=" + id + ", text=" + text + "]";
    }
}
