package com.example.proyectodws.Repository;

import com.example.proyectodws.Entities.Course;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class CourseRepository {
    private AtomicLong nextId = new AtomicLong(1L);
    private ConcurrentHashMap<Long, Course> courses = new ConcurrentHashMap<>();

    public List<Course> findAll() {
        return courses.values().stream().toList();
    }

    public Optional<Course> findById(long id) {
        return Optional.ofNullable(courses.get(id));
    }

    public void save(Course course) {
        long id = course.getId();
        if (id == 0) {
            id = nextId.getAndIncrement();
            course.setId(id);
        }
        courses.put(id, course);
    }

    public void deleteById(long id) {
        courses.remove(id);
    }

}
