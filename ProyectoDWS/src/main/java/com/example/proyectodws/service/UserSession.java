package com.example.proyectodws.service;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

// Session for users.
@Component
@SessionScope
public class UserSession {

    private int numCourses;
    private int numSubjects;

    public UserSession(){

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

}
