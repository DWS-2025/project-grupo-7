package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {
    private List<Subject> subjects = new ArrayList<>();
    private Long nextId = 1L;

    public List<Subject> getAllSubject(){
        return subjects;
    }

    public Subject getSubjectById(Long id) {
        return subjects.stream().filter(subject -> subject.getId().equals(id)).findFirst().orElse(null);
    }

    public void saveSubject(Subject subject) {
        subject.setId(nextId++);
        subjects.add(subject);
    }

    public void updateSubject(Long id, Subject subject) {
        Subject existingSubject = getSubjectById(id);
        if (existingSubject != null) {
            existingSubject.setTitle(subject.getTitle());
            existingSubject.setText(subject.getText());
        }
    }

    public void deleteSubject(Long id) {
        subjects.removeIf(subject -> subject.getId().equals(id));
    }
}

