package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashSet;
import java.util.Set;

@Component
@SessionScope
public class UserSession {

    private String user;
    private Set<Course> enrolledCourses = new HashSet<>();

    private int numCourses;
    private int numSubjects;


    public UserSession(){
        this.user="Equipo de administracion";
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


    public int getNumSubjects() {
        return numSubjects;
    }


    public void incNumSubjects() {
        this.numSubjects++;
    }

    public void disNumSubject() {
        this.numSubjects--;
    }

    public Set <Course> getEnrolledCourses() {

        return enrolledCourses;
    }

    public void enrollInCourse(Course course, User user) {

        enrolledCourses.add(course);
        course.enrollStudent(user);
    }



}
