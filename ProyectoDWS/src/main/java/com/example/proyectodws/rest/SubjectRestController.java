package com.example.proyectodws.rest;

import com.example.proyectodws.api.GenericResponse;
import com.example.proyectodws.api.ImageResponse;
import com.example.proyectodws.api.PagedSubjectsResponse;
import com.example.proyectodws.api.SubjectResponse;
import com.example.proyectodws.api.SubjectsResponse;
import com.example.proyectodws.dto.SubjectDTO;
import com.example.proyectodws.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectRestController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public ResponseEntity<PagedSubjectsResponse> getSubjects(@RequestParam int page, @RequestParam int size) {
        List<SubjectDTO> subjects = subjectService.getSubjects(PageRequest.of(page -1, size));
        return ResponseEntity.ok(new PagedSubjectsResponse(new GenericResponse("Asignaturas obtenidas correctamente", 200), subjects, page, (int) Math.ceil(subjectService.getAllSubjects().size() / (double) size), size));
    }


    @GetMapping("/all")
    public ResponseEntity<SubjectsResponse> getAllSubjects() {
        return ResponseEntity.ok(new SubjectsResponse(new GenericResponse("Asignaturas obtenidas correctamente", 200), subjectService.getAllSubjects()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getSubjectById(@PathVariable Long id) {
        SubjectDTO subject = subjectService.getSubjectById(id);

        if (subject != null) {
            return ResponseEntity.ok(new SubjectResponse(new GenericResponse("Asignatura obtenida correctamente", 200), subject));
        } else {
            return ResponseEntity.status(404).body(new SubjectResponse(new GenericResponse("Asignatura no encontrada", 404), null));
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<ImageResponse> getSubjectImage(@PathVariable Long id) throws SQLException {

        SubjectDTO subject = subjectService.getSubjectById(id);
        if (subject == null) {
            return ResponseEntity.status(404).body(new ImageResponse(new GenericResponse("Asignatura no encontrada", 404), null));
        }

        Resource image = subjectService.getSubjectImage(id);
        if (image == null) {
            return ResponseEntity.status(404).body(new ImageResponse(new GenericResponse("Imagen no encontrada", 404), null));
        }

        try {
            byte[] imageBytes = image.getContentAsByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            return ResponseEntity.ok(new ImageResponse(new GenericResponse("Imagen obtenida correctamente", 200), "data:image/jpeg;base64," + base64Image));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new ImageResponse(new GenericResponse("Error al obtener la imagen", 500), null));
        }
    }


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<SubjectResponse> createSubjectWithImage(
            @RequestParam String title,
            @RequestParam String text,
            @RequestPart MultipartFile image) throws IOException, SQLException {

        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new SubjectResponse(new GenericResponse("El título es obligatorio", 400), null));
        }
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new SubjectResponse(new GenericResponse("El texto es obligatorio", 400), null));
        }
        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().body(new SubjectResponse(new GenericResponse("La imagen es obligatoria", 400), null));
        }
        if (image != null && !image.isEmpty()) {
            if (image.getSize() > 5_000_000) {
                return ResponseEntity.badRequest().body(new SubjectResponse(new GenericResponse("La imagen debe tener un tamaño menor a 5MB", 400), null));
            }
        }

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

        return ResponseEntity.ok(new SubjectResponse(new GenericResponse("Asignatura creada correctamente", 200), saved));
    }


    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> updateSubject(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String text) {

        SubjectDTO existingSubject = subjectService.getSubjectById(id);
        if (existingSubject == null) {
            return ResponseEntity.status(404).body(new SubjectResponse(new GenericResponse("Asignatura no encontrada", 404), null));
        }

        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new SubjectResponse(new GenericResponse("El título es obligatorio", 400), null));
        }

        // Validate text
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new SubjectResponse(new GenericResponse("El texto es obligatorio", 400), null));
        }

        SubjectDTO subject = new SubjectDTO(id, title, text, null);
        SubjectDTO updated = subjectService.updateSubject(id, subject);
        return ResponseEntity.ok(new SubjectResponse(new GenericResponse("Asignatura actualizada correctamente", 200), updated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse> deleteSubject(@PathVariable Long id) {

        SubjectDTO existingSubject = subjectService.getSubjectById(id);
        if (existingSubject == null) {
            return ResponseEntity.status(404).body(new GenericResponse("Asignatura no encontrada", 404));
        }

        subjectService.deleteSubject(id);
        return ResponseEntity.ok(new GenericResponse("Asignatura eliminada correctamente", 200));
    }
}
