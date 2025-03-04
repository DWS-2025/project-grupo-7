package com.example.proyectodws.Entities;

import java.util.ArrayList;
import java.util.List;


public class Course {
    private Long id;
    private String subject;
    private String title;
    private String description;

    private List<String> enrolledStudents = new ArrayList<>();


    public Course(){

    }

    public Course(String subject, String title, String description) {
        super();
        this.subject=subject;
        this.title = title;
        this.description = description;
    }



    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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
                ", subject='" + subject + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
