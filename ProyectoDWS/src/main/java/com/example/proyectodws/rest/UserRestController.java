package com.example.proyectodws.rest;

import com.example.proyectodws.api.CoursesResponse;
import com.example.proyectodws.api.GenericResponse;
import com.example.proyectodws.api.ImageResponse;
import com.example.proyectodws.api.UserResponse;
import com.example.proyectodws.api.UsersResponse;
import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.dto.UserWithoutPasswordDTO;
import com.example.proyectodws.service.UserService;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<UsersResponse> getAllUsers() {
        List<UserWithoutPasswordDTO> usersWithoutPassword = userService.getAllUsers().stream()
                .map(user -> new UserWithoutPasswordDTO(user.id(), user.first_name(), user.last_name(), user.username()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new UsersResponse(new GenericResponse("Usuarios obtenidos correctamente", 200), usersWithoutPassword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(new UserResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }

        UserWithoutPasswordDTO userWithoutPassword = new UserWithoutPasswordDTO(user.id(), user.first_name(), user.last_name(), user.username());

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario obtenido correctamente", 200), userWithoutPassword));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        Resource user = userService.getUserImage(id);
        byte[] imageBytes;

        // Check if user exists
        UserDTO userDTO = userService.getUserById(id);
        if (userDTO == null) {
            return ResponseEntity.status(404).body(new ImageResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }

        // Check permissions (admin can see all images but user only his own)
        UserDTO loggedUser = userService.getLoggedUserDTO();
        if (!loggedUser.id().equals(id) && !loggedUser.roles().contains("ADMIN")) {
            return ResponseEntity.status(403).body(new ImageResponse(new GenericResponse("No tienes permisos para ver esta imagen", 403), null));
        }

        try {
            imageBytes = user.getInputStream().readAllBytes();
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new ImageResponse(new GenericResponse("Error al obtener la imagen", 500), null));
        }

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String dataUrl = "data:image/jpeg;base64," + base64Image;
        return ResponseEntity.ok(new ImageResponse(new GenericResponse("Imagen obtenida correctamente", 200), dataUrl));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        // Get authenticated user from JWT token
        UserDTO user = userService.getLoggedUserDTO();

        UserWithoutPasswordDTO userWithoutPassword = new UserWithoutPasswordDTO(user.id(), user.first_name(), user.last_name(), user.username());

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario obtenido correctamente", 200), userWithoutPassword));
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<CoursesResponse> getUserCourses(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);

        // Check if user exists
        if (user == null) {
            return ResponseEntity.status(404).body(new CoursesResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }

        // Get user courses
        List<CourseDTO> courses = user.courses();

        return ResponseEntity.ok(new CoursesResponse(new GenericResponse("Cursos obtenidos correctamente", 200), courses));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> createUser(
            @RequestPart String username,
            @RequestPart String first_name,
            @RequestPart String last_name,
            @RequestPart String password,
            @RequestPart MultipartFile image,
            @RequestPart(required = false) String roles
    ) {

        // Use jsoup to clean the input values.
        username = Jsoup.clean(username, "", Safelist.none());
        first_name = Jsoup.clean(first_name, "", Safelist.none());
        last_name = Jsoup.clean(last_name, "", Safelist.none());
        password = Jsoup.clean(password, "", Safelist.none());

        // Validate required fields
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre de usuario es requerido", 400), null));
        }

        if (password == null || password.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("La contraseña es requerida", 400), null));
        }

        if (first_name == null || first_name.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre es requerido", 400), null));
        }

        if (last_name == null || last_name.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El apellido es requerido", 400), null));
        }

        // Validate image
        if (image == null || image.isEmpty()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("La imagen es requerida", 400), null));
        }

        // Validate image size.
        if (image.getSize() > 1024 * 1024 * 5) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("La imagen no puede pesar más de 5MB", 400), null));
        }

        // Check if username already exists
        if (userService.getUserByUsername(username) != null) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre de usuario ya existe", 400), null));
        }

        // Encode password.
        String hashedPassword = new BCryptPasswordEncoder().encode(password);

        // Add user role.
        List<String> rolesList = new ArrayList<>();
        if (roles == null || roles.isEmpty()) {
            rolesList.add("USER");
        } else {
            String[] roleArray = roles.split(",");
            for (String role : roleArray) {
                rolesList.add(role.trim());
            }
        }

        // Create user
        UserDTO user = new UserDTO(null, first_name, last_name, username, hashedPassword, null, rolesList, null);
        UserDTO createdUser;

        if (image != null && !image.isEmpty()) {
            try {
                createdUser = userService.createWithImage(user, image);
            } catch (IOException e) {
                return ResponseEntity.status(500).body(new UserResponse(new GenericResponse("Error al guardar la imagen", 500), null));
            } catch (SQLException e) {
                return ResponseEntity.status(500).body(new UserResponse(new GenericResponse("Error al guardar la imagen", 500), null));
            }
        } else {
            createdUser = userService.createUser(user);
        }

        UserWithoutPasswordDTO userWithoutPassword = new UserWithoutPasswordDTO(createdUser.id(), createdUser.first_name(), createdUser.last_name(), createdUser.username());

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario creado correctamente", 200), userWithoutPassword));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestPart String first_name,
            @RequestPart String last_name,
            @RequestPart String username,
            @RequestPart(required = false) String password,
            @RequestPart(required = false) MultipartFile image,
            @RequestPart(required = false) String roles) {

        // Use jsoup to clean the input values.
        first_name = Jsoup.clean(first_name, "", Safelist.none());
        last_name = Jsoup.clean(last_name, "", Safelist.none());
        username = Jsoup.clean(username, "", Safelist.none());
        password = Jsoup.clean(password, "", Safelist.none());

        // Validate required fields
        if (first_name == null || first_name.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre es requerido", 400), null));
        }

        if (last_name == null || last_name.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El apellido es requerido", 400), null));
        }

        if (username == null || username.isBlank()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre de usuario es requerido", 400), null));
        }

        // Validate image
        if (image == null || image.isEmpty()) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("La imagen es requerida", 400), null));
        }

        // Validate image size.
        if (image.getSize() > 1024 * 1024 * 5) {
            return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("La imagen no puede pesar más de 5MB", 400), null));
        }

        // Get old user
        UserDTO oldUser = userService.getUserById(id);

        if (oldUser == null) {
            return ResponseEntity.status(404).body(new UserResponse(new GenericResponse("Usuario no encontrado", 404), null));
        }

        // Check if username already exists
        if (username != oldUser.username()) {
            UserDTO existingUser = userService.getUserByUsername(username);
            if (existingUser != null && existingUser.id() != oldUser.id()) {
                return ResponseEntity.status(400).body(new UserResponse(new GenericResponse("El nombre de usuario ya existe", 400), null));
            }
        }

        String encodedPassword = password != null && !password.isEmpty() ?
                new BCryptPasswordEncoder().encode(password) :
                null;

        List<String> rolesList = new ArrayList<>();
        if (roles == null || roles.isEmpty()) {
            rolesList.add("USER");
        } else {
            String[] roleArray = roles.split(",");
            for (String role : roleArray) {
                rolesList.add(role.trim());
            }
        }

        UserDTO newUser = new UserDTO(
                oldUser.id(),
                first_name,
                last_name,
                username,
                encodedPassword,
                null,
                rolesList,
                null);

        UserDTO updatedUser;

        if (image != null && !image.isEmpty()) {
            try {
                updatedUser = userService.updateWithImage(oldUser.id(), newUser, image);
            } catch (IOException e) {
                return ResponseEntity.status(500).body(new UserResponse(new GenericResponse("Error al guardar la imagen", 500), null));
            } catch (SQLException e) {
                return ResponseEntity.status(500).body(new UserResponse(new GenericResponse("Error al guardar la imagen", 500), null));
            }
        } else {
            updatedUser = userService.updateUser(oldUser.id(), newUser);
        }

        UserWithoutPasswordDTO userWithoutPassword = new UserWithoutPasswordDTO(updatedUser.id(), updatedUser.first_name(), updatedUser.last_name(), updatedUser.username());

        return ResponseEntity.ok(new UserResponse(new GenericResponse("Usuario actualizado correctamente", 200), userWithoutPassword));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse> deleteUser(@PathVariable Long id) {

        // Check if user exists
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(new GenericResponse("Usuario no encontrado", 404));
        }

        UserDTO loggedUser = userService.getLoggedUserDTO();

        if (loggedUser.id().equals(id)) {
            return ResponseEntity.status(400).body(new GenericResponse("No puedes eliminar tu propio usuario", 400));
        }

        if (user.roles().contains("ADMIN")) {
            return ResponseEntity.status(403).body(new GenericResponse("No puedes eliminar un usuario administrador", 403));
        }

        // Delete user
        userService.deleteUser(id);

        return ResponseEntity.ok(new GenericResponse("Usuario eliminado correctamente", 200));
    }
}