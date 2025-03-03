package com.example.proyectodws.Entities;
import java.util.ArrayList;
import java.util.List;

//this code is for subjects
public class Subject {
    private Long id;
    private String title;
    private String text;

    private List<Course> associatedCourses = new ArrayList<>();

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

    @Override
    public String toString() {
        return "Class [id="+id+", title=" + title + ", text=" + text + "]";
    }

}
