package com.example.proyectodws.repository;


import com.example.proyectodws.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // find an user by username
    Optional<User> findByUsername(String username);
}