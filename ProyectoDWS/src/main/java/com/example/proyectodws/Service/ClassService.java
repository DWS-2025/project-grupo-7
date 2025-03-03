package com.example.proyectodws.Service;

import com.example.proyectodws.Entities.Subject;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
@Service
public class ClassService {
    private ConcurrentMap<Long, Subject> classes = new ConcurrentHashMap<>();
    private AtomicLong nextId = new AtomicLong(1);

    public ClassService() {
    }

    // Look for and return all languages
    public Collection<Subject> findAll() {

        return classes.values();
    }

    // Look for and return the language by ID
    public Subject findById(long id) {

        return classes.get(id);
    }

    // Keep the language / puts new ID
    public void save(Subject class1) {

        if(class1.getId() == null || class1.getId() == 0) {
            long id = nextId.getAndIncrement();
            class1.setId(id);
        }

        this.classes.put(class1.getId(), class1);
    }

    // Delete languages by ID
    public void deleteById(long id) {

        this.classes.remove(id);
    }

    public Subject getPostByLanguage(String language) {
        for (Subject class1 : classes.values()) {
            if (class1.getTitle().equals(language)) {
                return class1;
            }
        }
        return null; // if doesn't find anything
    }

}
