package com.example.proyectodws.service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.UUID;

@Service
public class MediaService {

    private static final Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");
    private Path createFilePath(long imageId, Path folder) {

        return folder.resolve("image-" + imageId + ".jpg");
    }

    //save image
    public String saveImage(MultipartFile image) throws IOException {

        Path directory = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "images");
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        String originalFilename = image.getOriginalFilename();
        String newFilename = "image_" + UUID.randomUUID() + "_" + originalFilename;
        Path filePath = directory.resolve(newFilename);
        Files.write(filePath, image.getBytes());

        return "/images/" + newFilename;
    }

    public String saveImage(Blob image) throws IOException, SQLException {
        Path directory = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "images");

        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String newFilename = "image_" + UUID.randomUUID() + ".jpg";

        Path filePath = directory.resolve(newFilename);
        Files.write(filePath, image.getBytes(1, (int) image.length()));

        return "/images/" + newFilename;
    }

    public String saveVideo(Long courseId, MultipartFile video) throws IOException {

        Path directory = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "videos", courseId.toString());

        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String originalFilename = video.getOriginalFilename();

        String newFilename = originalFilename;

        Path filePath = directory.resolve(newFilename);

        Files.write(filePath, video.getBytes());

        return "/videos/" + courseId.toString() + "/" + newFilename;
    }

    public ResponseEntity<Object> createResponseFromImage(String folderName, long imageId) throws MalformedURLException {

        Path folder = FILES_FOLDER.resolve(folderName);

        Path imagePath = createFilePath(imageId, folder);

        Resource file = new UrlResource(imagePath.toUri());

        if(!Files.exists(imagePath)) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(file);
        }
    }

    public void deleteImage(String folderName, long imageId) throws IOException {
        Path folder = FILES_FOLDER.resolve(folderName);
        Path imageFile = createFilePath(imageId, folder);
        Files.deleteIfExists(imageFile);
    }


    public Resource getImage(String imageName) {
        Path imagePath = FILES_FOLDER.resolve(imageName);
        try {
            return new UrlResource(imagePath.toUri());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't get local image");
        }
    }

    public String createImage(MultipartFile multiPartFile) {
        String originalName = multiPartFile.getOriginalFilename();
        if(!originalName.matches(".*\\.(jpg|jpeg|gif|png)")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The url is not an image resource");
        }
        String fileName = "image_" + UUID.randomUUID() + "_" +originalName;
        Path imagePath = FILES_FOLDER.resolve(fileName);
        try {
            multiPartFile.transferTo(imagePath);
        } catch (Exception ex) {
            System.err.println(ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't save image locally", ex);
        }
        return fileName;
    }

    public Blob filePathToBlob(String resourcePath) throws IOException, SQLException {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try (InputStream is = resource.getInputStream()) {
            byte[] bytes = is.readAllBytes();
            return new SerialBlob(bytes);
        }
    }
    public static Blob convertToBlob(byte[] bytes) throws SQLException {
        return new SerialBlob(bytes);
    }
}