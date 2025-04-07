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

    public Subject save (Subject subject, MultipartFile imageFile) throws IOException, SQLException {
        if (imageFile != null && !imageFile.isEmpty()) {
            // Convertir la imagen a Blob
            byte[] imageBytes = imageFile.getBytes();
            Blob blob = new SerialBlob(imageBytes);
            subject.setImageFile(blob);  // Asignamos el Blob de la imagen a la asignatura
        }
        return subjectRepository.save(subject);  // Guardamos la asignatura con la imagen
    }
    public void updateSubject(Subject subject) {
        // Aquí se actualiza la asignatura en la base de datos
        subjectRepository.save(subject); // Esto guarda la asignatura en la base de datos
    }

}

