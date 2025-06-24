package com.example.proyectodws.rest;

import com.example.proyectodws.api.CourseResponse;
import com.example.proyectodws.api.CoursesResponse;
import com.example.proyectodws.api.GenericResponse;
import com.example.proyectodws.api.ImageResponse;
import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.SubjectDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.service.CourseService;
import com.example.proyectodws.service.SubjectService;
import com.example.proyectodws.service.UserService;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseRestController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<CoursesResponse> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();

        GenericResponse genericResponse = new GenericResponse("Cursos obtenidos correctamente", 200);
        return ResponseEntity.ok(new CoursesResponse(genericResponse, courses));
    }

    @GetMapping("/search")
    public ResponseEntity<CoursesResponse> searchCourses(@RequestParam String search) {

        // Use jsoup to clean the input values.
        search = Jsoup.clean(search, "", Safelist.none());

        List<CourseDTO> courses = courseService.findCoursesByTitles(search, search);
        GenericResponse genericResponse = new GenericResponse("Cursos obtenidos correctamente", 200);
        return ResponseEntity.ok(new CoursesResponse(genericResponse, courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);

        ResponseEntity<CourseResponse> response = null;
        if (course != null) {
            response = ResponseEntity.ok(new CourseResponse(new GenericResponse("Curso obtenido correctamente", 200), course));
        } else {
            response = ResponseEntity.status(404).body(new CourseResponse(new GenericResponse("Curso no encontrado", 404), null));
        }

        return response;
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<ImageResponse> getCourseImage(@PathVariable Long id) throws SQLException {
        Resource image = courseService.getCourseImage(id);
        byte[] imageBytes = null;

        ResponseEntity<ImageResponse> response = null;

        try {
            imageBytes = image.getInputStream().readAllBytes();
        } catch (IOException e) {
            return ResponseEntity
                    .status(404)
                    .body(new ImageResponse(new GenericResponse("Imagen del curso no encontrada", 404), ""));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(new ImageResponse(new GenericResponse("Error interno del servidor", 500), ""));
        }
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        response = ResponseEntity.ok(new ImageResponse(new GenericResponse("Imagen del curso obtenida correctamente", 200), base64Image));

        return response;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<CourseResponse> createCourseByParams(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam List<Long> subjectIds,
            @RequestPart MultipartFile image,
            @RequestPart MultipartFile video) throws IOException, SQLException {

        // Use jsoup to clean the input values.
        title = Jsoup.clean(title, "", Safelist.none());
        description = Jsoup.clean(description, "", Safelist.none());

        // Validate title
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new CourseResponse(new GenericResponse("El título es obligatorio", 400), null));
        }

        // Validate description
        if (description == null || description.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new CourseResponse(new GenericResponse("La descripción es obligatoria", 400), null));
        }

        // Validate subject IDs
        if (subjectIds == null || subjectIds.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new CourseResponse(new GenericResponse("Debe seleccionar al menos una asignatura", 400), null));
        }

        // Validate image
        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new CourseResponse(new GenericResponse("Debe proporcionar una imagen", 400), null));
        }

        // Validate video
        if (video == null || video.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new CourseResponse(new GenericResponse("Debe proporcionar un video", 400), null));
        }

        // Validate subject IDs if provided
        if (subjectIds != null) {
            // Check if any subject ID is null or negative
            if (subjectIds.stream().anyMatch(id -> id == null || id < 0)) {
                return ResponseEntity.badRequest().body(new CourseResponse(new GenericResponse("Las asignaturas deben ser válidas", 400), null));
            }
        }

        // Validate image if provided
        if (image != null && !image.isEmpty()) {
            // Check file size (e.g., max 5MB)
            if (image.getSize() > 5_000_000) {
                return ResponseEntity.badRequest().body(new CourseResponse(new GenericResponse("La imagen debe tener un tamaño menor a 5MB", 400), null));
            }

            // Check content type
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(new CourseResponse(new GenericResponse("La imagen debe ser un archivo de imagen", 400), null));
            }
        }

        List<SubjectDTO> subjects = subjectIds.stream()
                .map(id -> subjectService.getSubjectById(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        CourseDTO course = new CourseDTO(
                null,
                title,
                description,
                image.getOriginalFilename(),
                false,
                subjects,
                video.getOriginalFilename()
        );

        CourseDTO saved = null;
        if ((image != null && !image.isEmpty()) || (video != null && !video.isEmpty())) {
            saved = courseService.createWithMedia(course, image, video);
        }
        else {
            saved = courseService.saveCourse(course);
        }

        return ResponseEntity.ok(new CourseResponse(new GenericResponse("Curso creado correctamente", 200), saved));
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<CourseResponse> enrollInCourse(
            @PathVariable Long id
    ) {

        UserDTO user = userService.getLoggedUserDTO();

        // Check if course exists
        CourseDTO course = courseService.getCourseById(id);
        if (course == null) {
            return ResponseEntity.status(404).body(new CourseResponse(new GenericResponse("Curso no encontrado", 404), null));
        }

        // Check if user is already enrolled in course
        if (user.courses().contains(course)) {
            return ResponseEntity.status(400).body(new CourseResponse(new GenericResponse("Ya estás inscrito en este curso", 400), null));
        }

        // Enroll user in course
        userService.enrollUserInCourse(user.id(), id);

        return ResponseEntity.ok(new CourseResponse(new GenericResponse("Te has inscrito al curso correctamente", 200), course));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourseByParams(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam List<Long> subjectIds) throws IOException, SQLException {

        // Use jsoup to clean the input values.
        title = Jsoup.clean(title, "", Safelist.none());
        description = Jsoup.clean(description, "", Safelist.none());

        // Check if course exists
        CourseDTO existingCourse = courseService.getCourseById(id);
        if (existingCourse == null) {
            return ResponseEntity.status(404).body(new CourseResponse(new GenericResponse("Curso no encontrado", 404), null));
        }

        // Validate title
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new CourseResponse(new GenericResponse("El título es obligatorio", 400), null));
        }

        // Validate description
        if (description == null || description.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new CourseResponse(new GenericResponse("La descripción es obligatoria", 400), null));
        }

        // Validate subject IDs
        if (subjectIds == null || subjectIds.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new CourseResponse(new GenericResponse("Debe seleccionar al menos una asignatura", 400), null));
        }

        // Validate subject IDs if provided
        if (subjectIds != null) {
            // Check if any subject ID is null or negative
            if (subjectIds.stream().anyMatch(subjectId -> subjectId == null || subjectId < 0)) {
                return ResponseEntity.badRequest().body(new CourseResponse(new GenericResponse("Las asignaturas deben ser válidas", 400), null));
            }
        }

        List<SubjectDTO> subjects = subjectIds.stream()
                .map(subjectId -> subjectService.getSubjectById(subjectId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        CourseDTO newCourse = new CourseDTO(
                id,
                title,
                description,
                null,
                false,
                subjects,
                null
        );

        CourseDTO updated = courseService.updateCourse(id, newCourse);
        return ResponseEntity.ok(new CourseResponse(new GenericResponse("Curso actualizado correctamente", 200), updated));
    }

    @PutMapping("/{id}/unenroll")
    public ResponseEntity<CourseResponse> unenrollUserFromCourse(
            @PathVariable Long id
    ) {

        UserDTO user = userService.getLoggedUserDTO();

        // Check if course exists
        CourseDTO course = courseService.getCourseById(id);
        if (course == null) {
            return ResponseEntity.status(404).body(new CourseResponse(new GenericResponse("Curso no encontrado", 404), null));
        }

        // Check if user is enrolled in course
        if (!user.courses().contains(course)) {
            return ResponseEntity.status(400).body(new CourseResponse(new GenericResponse("No estás inscrito en este curso", 400), null));
        }

        // Unenroll user from course
        userService.unenrollUserFromCourse(user.id(), id);

        return ResponseEntity.ok(new CourseResponse(new GenericResponse("Te has desinscrito del curso correctamente", 200), course));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse> deleteCourse(@PathVariable Long id) {

        // Check if course exists
        CourseDTO existingCourse = courseService.getCourseById(id);
        if (existingCourse == null) {
            return ResponseEntity.status(404).body(new GenericResponse("Curso no encontrado", 404));
        }

        // Delete course
        courseService.deleteCourse(id);
        return ResponseEntity.ok(new GenericResponse("Curso eliminado correctamente", 200));
    }
}
