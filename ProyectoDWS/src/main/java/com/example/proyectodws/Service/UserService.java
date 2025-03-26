package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.Subject;
import com.example.proyectodws.Entities.User;
import com.example.proyectodws.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private String user;
    private Set<Course> enrolledCourses = new HashSet<>();
    private Set<Subject> enrolledSubjects = new HashSet<>();

    private int numCourses;
    private int numSubjects;

    @Autowired
    private UserRepository userRepository;

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
    public Set <Subject> getEnrolledSubjects() {

        return enrolledSubjects;
    }

    public void enrollInCourse(Course course) {

        enrolledCourses.add(course);
        course.enrollStudent(this.user);
    }
    public void enrollInSubject(Subject subject) {

        enrolledSubjects.add(subject);
        subject.enrollStudent(this.user);
    }


    public void removeCourseFromUsers(Course course) {
        enrolledCourses.remove(course);
    }


    public User getLoggedUser() {
        return userRepository.findAll().get(0);
    }


}

