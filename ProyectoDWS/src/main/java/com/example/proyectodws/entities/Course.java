package com.example.proyectodws.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.sql.Blob;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String imagePath;
    private Boolean isFeatured = false;
    private String video;

    @Lob
    @JsonIgnore
    private Blob imageFile;

    @ManyToMany(mappedBy = "courses")
    @JsonBackReference
    private List<User> enrolledStudents = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "course_subject",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    @JsonManagedReference
    private List<Subject> subjects = new ArrayList<>();


    public Course(){

    }

    public Course(String title, String description) {
        super();
        this.title = title;
        this.description = description;
    }

    public Course(String title, String description, Boolean isFeatured, String video) {
        super();
        this.title = title;
        this.description = description;
        this.isFeatured = isFeatured;
        this.video = video;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public void addSubject(Subject subject) {
        this.subjects.add(subject);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public List<User> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<User> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public void enrollStudent(User user) {
        enrolledStudents.add(user);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getImage() {
        return imagePath;
    }

    public void setImage(String image) {
        this.imagePath = image;
    }

    public Blob getImageFile() {
        return imageFile;
    }

    public void setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    @Lob
    private byte[] imageData;

    // Getters y setters
   /* public byte[] getImageData() {
        return imageData;
    }*/

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setCourse(this);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
