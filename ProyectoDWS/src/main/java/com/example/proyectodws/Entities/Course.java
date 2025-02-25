package com.example.proyectodws.Entities;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private Long id;
    private String language;
    private String title;
    private String description;

    private List<String> enrolledStudents = new ArrayList<>();


    public Course(){

    }

    public Course(String language, String title, String description) {
        super();
        this.language=language;
        this.title = title;
        this.description = description;
    }



    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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


    public List<String> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<String> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public void enrollStudent(String studentName) {
        enrolledStudents.add(studentName);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", language='" + language + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
