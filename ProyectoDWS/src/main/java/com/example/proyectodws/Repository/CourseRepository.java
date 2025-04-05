package com.example.proyectodws.Repository;

import com.example.proyectodws.Entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c " +
            "JOIN c.subject l " +
            "WHERE l.title LIKE %:subjectTitle% AND c.title LIKE %:courseTitle%")
    List<Course> findCoursesByTitles(@Param("subjectTitle") String subjectTitle,
                                     @Param("courseTitle") String courseTitle);
}
