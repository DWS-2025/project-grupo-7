package com.example.proyectodws.Repository;


import com.example.proyectodws.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Buscar un usuario por su nombre de usuario
    Optional<User> findByUsername(String username);
}

