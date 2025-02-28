package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Course;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserSession {
    private String user;
    private Set<Course> enrolledCourses = new HashSet<>();

    private int numCourses;
    private int numLanguages;


    public UserSession(){
        this.user="Equipo de administración";
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public int getNumCourses() {
        return this.numCourses;
    }

    public void incNumCourses() {
        this.numCourses++;
    }

    public void disNumCourses() {
        this.numCourses--;
    }


    public int getNumLanguages() {
        return numLanguages;
    }


    public void incNumLanguages() {
        this.numLanguages++;
    }

    public void disNumLanguage() {
        this.numLanguages--;
    }

    public Set <Course> getEnrolledCourses() {

        return enrolledCourses;
    }

    public void enrollInCourse(Course course) {

        enrolledCourses.add(course);
        course.enrollStudent(this.user);
    }



}

