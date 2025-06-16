package com.example.proyectodws.service;

import com.example.proyectodws.entities.Subject;
import com.example.proyectodws.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CourseService courseService;

    public Subject createSubject(Subject subject){

        return subjectRepository.save(subject);
    }

    public Subject getSubjectById(Long id){
        Optional<Subject> optionalSubject = subjectRepository.findById(id);
        return optionalSubject.orElse(null);
    }

    public List<Subject> getAllSubjects(){

        return subjectRepository.findAll();
    }

    public void deleteSubject (Long id){
        Subject subject = subjectRepository.findById(id).orElse(null);
        if (subject != null) {
            subject.getAssociatedCourses().forEach(course -> {
                course.getSubjects().remove(subject);
                try {
                    courseService.save(course, null);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            });
            subjectRepository.deleteById(id);
        }
    }

    public Subject save (Subject subject, MultipartFile imageFile) throws IOException, SQLException {
        if (imageFile != null && !imageFile.isEmpty()) {

            byte[] imageBytes = imageFile.getBytes();
            Blob blob = new SerialBlob(imageBytes);
            subject.setImageFile(blob);
        }
        return subjectRepository.save(subject);
    }
    public void updateSubject(Subject subject) {

        subjectRepository.save(subject);
    }
    public Page<Subject> getSubjects(PageRequest pageRequest) {
        return subjectRepository.findAll(pageRequest);
    }

}
