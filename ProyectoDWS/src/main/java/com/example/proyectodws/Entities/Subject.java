package com.example.proyectodws.Entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

//this code is for subjects
 @Entity
 @Table(name = "Subject")
 public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String text;
    private String imagePath;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Course> associatedCourses = new ArrayList<>();

    @Lob
    @JsonIgnore
    private Blob imageFile;

    public Subject(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public Subject() {
    }

    public List<Course> getAssociatedCourses() {
        return associatedCourses;
    }

    public void setAssociatedCourses(List<Course> associatedCourses) {
        this.associatedCourses = associatedCourses;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    @Override
    public String toString() {
        return "Class [id="+id+", title=" + title + ", text=" + text + "]";
    }

}
