package com.example.proyectodws.RestControllers;

import com.example.proyectodws.Entities.Subject;
import com.example.proyectodws.Service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectRestController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<Subject>> getSubjects(@RequestParam int page, @RequestParam int size) {
        Page<Subject> subjectPage = subjectService.getSubjects(PageRequest.of(page, size));
        return ResponseEntity.ok(subjectPage.getContent());
    }


    @GetMapping("/all")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        Subject subject = subjectService.getSubjectById(id);
        return subject != null ? ResponseEntity.ok(subject) : ResponseEntity.notFound().build();
    }


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Subject> createSubjectWithImage(
            @RequestParam String title,
            @RequestParam String text,
            @RequestPart(required = false) MultipartFile image) throws IOException {

        Subject subject = new Subject();
        subject.setTitle(title);
        subject.setText(text);

        if (image != null && !image.isEmpty()) {
            subject.setImageData(image.getBytes());
        }

        Subject saved = subjectService.createSubject(subject);
        return ResponseEntity.ok(saved);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String text) {

        Subject existing = subjectService.getSubjectById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setTitle(title);
        existing.setText(text);
        subjectService.updateSubject(existing);
        return ResponseEntity.ok(existing);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}
