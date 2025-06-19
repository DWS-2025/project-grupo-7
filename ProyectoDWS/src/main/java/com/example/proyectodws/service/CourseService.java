package com.example.proyectodws.service;

import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.CourseMapper;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.dto.UserMapper;
import com.example.proyectodws.entities.Course;
import com.example.proyectodws.entities.User;
import com.example.proyectodws.repository.CourseRepository;
import com.example.proyectodws.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.sql.rowset.serial.SerialBlob;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

// Service for courses.
@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MediaService mediaService;

    // Look for and return all courses
    public CourseDTO createWithMedia(CourseDTO courseDTO, MultipartFile image, MultipartFile video) throws IOException, SQLException {
        Course course = courseMapper.toDomain(courseDTO);

        if (image != null && !image.isEmpty()) {
            byte[] imageBytes = image.getBytes();
            Blob blob = new SerialBlob(imageBytes);
            course.setImageFile(blob);
        }

        Course saved = courseRepository.save(course);

        if (video != null && !video.isEmpty()) {
            saved.setVideo(mediaService.saveVideo(saved.getId(), video));
        }

        saved = courseRepository.save(saved);

        return courseMapper.toDTO(saved);
    }

    public CourseDTO saveCourse(CourseDTO courseDTO) {
        Course course = courseMapper.toDomain(courseDTO);
        return courseMapper.toDTO(courseRepository.save(course));
    }

    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course oldCourse = courseRepository.findById(id).orElse(null);
        Course newCourse = courseMapper.toDomain(courseDTO);
        oldCourse.setTitle(newCourse.getTitle());
        oldCourse.setDescription(newCourse.getDescription());
        oldCourse.setSubjects(newCourse.getSubjects());

        return courseMapper.toDTO(courseRepository.save(oldCourse));
    }

    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElse(null);
        return courseMapper.toDTO(course);
    }

    public List<CourseDTO> getAllCourses(){
        return courseMapper.toDTOs(courseRepository.findAll());
    }

    public void deleteCourse (Long id){
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();

            List<User> enrolledStudents = course.getEnrolledStudents();
            for (User user : enrolledStudents) {
                user.getCourses().remove(course);
                userRepository.save(user);
            }
            courseRepository.deleteById(id);
        }
    }

    public List<CourseDTO> findCoursesByTitles(String languageTitle, String courseTitle) {
        return courseMapper.toDTOs(courseRepository.findCoursesByTitles(languageTitle, courseTitle));
    }

    public List<CourseDTO> getFeaturedCourses() {
        return courseMapper.toDTOs(courseRepository.findByIsFeaturedTrue());
    }

    public Resource getCourseImage(Long id) throws SQLException {
        Course course = courseRepository.findById(id).orElse(null);
        if (course.getImageFile() != null) {
            Resource file = new InputStreamResource(course.getImageFile().getBinaryStream());

            return file;
        } else {
            throw new NoSuchElementException("Image not found");
        }
    }

    public List<UserDTO> getEnrolledStudents(Long id) {
        Course course = courseRepository.findById(id).orElse(null);
        return userMapper.toDTOs(course.getEnrolledStudents());
    }

    public int getNumCourses() {
        return courseRepository.findAll().size();
    }
}
