package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Course;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CourseService {
    private ConcurrentMap<Long, Course> courses= new ConcurrentHashMap<>();

    private AtomicLong nextId = new AtomicLong(1);

    public CourseService() {
    }
    // Look for and return all courses
    public Collection<Course> findAll() {

        return courses.values();
    }
    // Look for and return the course with the 'ID'
    public Course findById(long id) {

        return courses.get(id);
    }
    // Keep the new course and increment the number of total courses
    public void save(Course course) {
        if(course.getId() == null || course.getId() == 0) {
            long id = nextId.getAndIncrement();
            course.setId(id);
        }

        this.courses.put(course.getId(), course);
    }
    // Delete a course by ID
    public void deleteById(long id) {
        this.courses.remove(id);
    }


}
