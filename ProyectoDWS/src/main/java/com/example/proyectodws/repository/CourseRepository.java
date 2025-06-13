package com.example.proyectodws.repository;

import com.example.proyectodws.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c " +
            "JOIN c.subject l " +
            "WHERE l.title LIKE %:subjectTitle% AND c.title LIKE %:courseTitle%")
    List<Course> findCoursesByTitles(@Param("subjectTitle") String subjectTitle,
                                     @Param("courseTitle") String courseTitle);

    @Query("SELECT c FROM Course c WHERE c.isFeatured = true")
    List<Course> findByIsFeaturedTrue();

}
