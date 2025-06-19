package com.example.proyectodws.service;

import com.example.proyectodws.dto.CourseMapper;
import com.example.proyectodws.dto.SubjectDTO;
import com.example.proyectodws.dto.SubjectMapper;
import com.example.proyectodws.entities.Subject;
import com.example.proyectodws.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private CourseMapper courseMapper;

    public SubjectDTO createSubject(SubjectDTO subjectDTO){
        Subject subject = subjectMapper.toDomain(subjectDTO);
        return subjectMapper.toDTO(subjectRepository.save(subject));
    }

    public SubjectDTO getSubjectById(Long id){
        Optional<Subject> optionalSubject = subjectRepository.findById(id);
        return subjectMapper.toDTO(optionalSubject.orElse(null));
    }

    public List<SubjectDTO> getAllSubjects(){

        return subjectMapper.toDTOs(subjectRepository.findAll());
    }

    public void deleteSubject (Long id){
        Subject subject = subjectRepository.findById(id).orElse(null);
        if (subject != null) {
            subject.getCourses().forEach(course -> {
                course.getSubjects().remove(subject);
                courseService.saveCourse(courseMapper.toDTO(course));
            });
            subjectRepository.deleteById(id);
        }
    }

    public SubjectDTO saveSubject(SubjectDTO subjectDTO) {
        Subject subject = subjectMapper.toDomain(subjectDTO);
        return subjectMapper.toDTO(subjectRepository.save(subject));
    }

    public SubjectDTO createWithImage(SubjectDTO subjectDTO, MultipartFile image) throws IOException, SQLException {
        Subject subject = subjectMapper.toDomain(subjectDTO);
        if (image != null && !image.isEmpty()) {
            byte[] imageBytes = image.getBytes();
            Blob blob = new SerialBlob(imageBytes);
            subject.setImageFile(blob);
        }

        return subjectMapper.toDTO(subjectRepository.save(subject));
    }

    public SubjectDTO updateSubject(Long id, SubjectDTO subjectDTO) {
        Subject oldSubject = subjectRepository.findById(id).orElse(null);
        Subject newSubject = subjectMapper.toDomain(subjectDTO);
        oldSubject.setTitle(newSubject.getTitle());
        oldSubject.setText(newSubject.getText());
        return subjectMapper.toDTO(subjectRepository.save(oldSubject));
    }

    public List<SubjectDTO> getSubjects(PageRequest pageRequest) {
        Page<Subject> subjects = subjectRepository.findAll(pageRequest);
        return subjectMapper.toDTOs(subjects.getContent());
    }

    public Resource getSubjectImage(Long id) throws SQLException {
        Subject subject = subjectRepository.findById(id).orElse(null);
        if (subject.getImageFile() != null) {
            Resource file = new InputStreamResource(subject.getImageFile().getBinaryStream());

            return file;
        } else {
            throw new NoSuchElementException("Image not found");
        }
    }

}
