package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.Subject;
import com.example.proyectodws.Repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

