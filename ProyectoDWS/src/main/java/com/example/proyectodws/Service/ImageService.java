package com.example.proyectodws.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    private static final Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");

    // Return a new path for a image
    private Path createFilePath(long imageId, Path folder) {

        return folder.resolve("image-" + imageId + ".jpg");
    }
    // Keep the image in a folder
    public void saveImage(String folderName, long imageId, MultipartFile image) throws IOException {

        Path folder = FILES_FOLDER.resolve(folderName);

        try {
            Files.createDirectories(folder);
            Path newFile = createFilePath(imageId, folder);
            image.transferTo(newFile);

        } catch (IOException e) {
            throw new IOException("No se pudo guardar la imagen.", e);
        }
    }

    // Return a image to the user
    public ResponseEntity<Object> createResponseFromImage(String folderName, long imageId) {

        try {
            Path folder = FILES_FOLDER.resolve(folderName);
            Path imagePath = createFilePath(imageId, folder);

            if (!Files.exists(imagePath) || !Files.isReadable(imagePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource file = new UrlResource(imagePath.toUri());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(file);

        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().body("Error al cargar la imagen.");
        }
    }

    public void deleteImage(String folderName, long imageId) throws IOException {

        Path folder = FILES_FOLDER.resolve(folderName);

        Path imageFile = createFilePath(imageId, folder);

        Files.deleteIfExists(imageFile);
    }


}
