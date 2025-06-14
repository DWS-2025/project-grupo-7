package com.example.proyectodws.repository;

import com.example.proyectodws.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT DISTINCT c FROM Course c " +
            "JOIN c.subjects s " +
            "WHERE (s.title LIKE %:subjectTitle% OR c.title LIKE %:courseTitle%)")
    List<Course> findCoursesByTitles(@Param("subjectTitle") String subjectTitle,
                                     @Param("courseTitle") String courseTitle);

    @Query("SELECT c FROM Course c WHERE c.isFeatured = true")
    List<Course> findByIsFeaturedTrue();

}
