package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Course;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private String user;
    private Set<Course> enrolledCourses = new HashSet<>();

    private int numCourses;
    private int numSubjects;


    public UserService(){
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


    public int getNumSubjects() {
        return numSubjects;
    }


    public void incNumSubjects() {
        this.numSubjects++;
    }

    public void disNumSubjects() {
        this.numSubjects--;
    }

    public Set <Course> getEnrolledCourses() {

        return enrolledCourses;
    }

    public void enrollInCourse(Course course) {

        enrolledCourses.add(course);
        course.enrollStudent(this.user);
    }



}

