package com.example.proyectodws.rest;

import com.example.proyectodws.dto.SubjectDTO;
import com.example.proyectodws.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subjects")
public class SubjectRestController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSubjects(@RequestParam int page, @RequestParam int size) {
        List<SubjectDTO> subjects = subjectService.getSubjects(PageRequest.of(page -1, size));
        return ResponseEntity.ok(Map.of("subjects", subjects, "currentPage", page, "totalPages", (int) Math.ceil(subjectService.getAllSubjects().size() / (double) size), "subjectsPerPage", size));
    }


    @GetMapping("/all")
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }


    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable Long id) {
        SubjectDTO subject = subjectService.getSubjectById(id);
        return subject != null ? ResponseEntity.ok(subject) : ResponseEntity.notFound().build();
    }


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<SubjectDTO> createSubjectWithImage(
            @RequestParam String title,
            @RequestParam String text,
            @RequestPart(required = false) MultipartFile image) throws IOException, SQLException {

        SubjectDTO subject = new SubjectDTO(
                null,
                title,
                text,
                image.getOriginalFilename()
        );

        SubjectDTO saved = null;
        if (image != null && !image.isEmpty()) {
            saved = subjectService.createWithImage(subject, image);
        }
        else {
            saved = subjectService.saveSubject(subject);
        }

        return ResponseEntity.ok(saved);
    }


    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String text) {

        subjectService.updateSubject(id, new SubjectDTO(id, title, text, null));
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}