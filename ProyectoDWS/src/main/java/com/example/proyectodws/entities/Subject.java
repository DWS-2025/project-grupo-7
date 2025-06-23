package com.example.proyectodws.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

// Entity for subjects.
@Entity
@Table(name = "Subject")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String text;
    private String imagePath;

    @ManyToMany(mappedBy = "subjects")
    @JsonBackReference
    private List<Course> courses = new ArrayList<>();

    @Lob
    @JsonIgnore
    private Blob imageFile;

    public Subject(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public Subject() {
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
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

    @Lob
    private byte[] imageData;

    // Getters y setters
   /* public byte[] getImageData() {
        return imageData;
    }*/

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    @Override
    public String toString() {
        return "Class [id="+id+", title=" + title + ", text=" + text + "]";
    }

}
