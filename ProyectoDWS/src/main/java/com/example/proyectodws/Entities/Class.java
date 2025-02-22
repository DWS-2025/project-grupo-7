package com.example.proyectodws.Entities;
import java.util.ArrayList;
import java.util.List;

//this code is for subjects
public class Class {
    private long id;
    private String name;
    private String description;

    private List<User> alumnos = new ArrayList<>();

    public Class(Long id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
    }

}
