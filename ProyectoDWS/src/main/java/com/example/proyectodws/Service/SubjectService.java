package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.Subject;
import com.example.proyectodws.Repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ImageService imageService;

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

        subjectRepository.deleteById(id);
    }

    public Subject save (Subject subject, MultipartFile imageField) throws IOException, SQLException {
        if (imageField != null && !imageField.isEmpty()) {
            // Convert content to Blob
            String imageName = imageField.getOriginalFilename();
            subject.setImage(imageName);
            byte[] imageBytes = imageField.getBytes();
            Blob imageBlob = new SerialBlob(imageBytes);
            subject.setImageFile(imageBlob);
        } else {
            subject.setImage("no-image.png");
        }
        return subjectRepository.save(subject);
    }
    public void updateSubject(Subject subject) {
        // Aquí se actualiza la asignatura en la base de datos
        subjectRepository.save(subject); // Esto guarda la asignatura en la base de datos
    }

}

