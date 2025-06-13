package com.example.proyectodws.repository;

import com.example.proyectodws.entities.Comment;
import com.example.proyectodws.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    Page<Comment> findByCourse(Course course, Pageable pageable);

}

