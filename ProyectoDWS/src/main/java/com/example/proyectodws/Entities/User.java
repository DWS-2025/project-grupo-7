package com.example.proyectodws.Entities;



public class User {
    private int ID;
    private String nombre;
    private String apellidos;

    public User(String name, String surname){
        this.nombre=name;
        this.apellidos=surname;
    }
    public String getname(User a){
        return a.nombre;
    }
    public String getsurname(User a){
        return a.apellidos;
    }
}
