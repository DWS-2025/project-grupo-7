package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Course;
import com.example.proyectodws.Entities.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SubjectService {
    private ConcurrentMap<Long, Subject> subjects= new ConcurrentHashMap<>();
    private AtomicLong nextId = new AtomicLong(1);

    public Collection<Subject> findAll(){
        return subjects.values();
    }

    public Subject getSubjectById(Long id) {
        return subjects.get(id);
    }

    public void saveSubject(Subject subject) {
        if (subject.getId() == null || subject.getId() == 0) {
            long id = nextId.getAndIncrement();
            subject.setId(id);
        }

        this.subjects.put(subject.getId(), subject);
    }


    public void updateSubject(Long id, Subject subject) {
        Subject existingSubject = getSubjectById(id);
        if (existingSubject != null) {
            existingSubject.setTitle(subject.getTitle());
            existingSubject.setText(subject.getText());
        }
    }

    public void deleteSubject(long id) {
        this.subjects.remove(id);
    }


}

