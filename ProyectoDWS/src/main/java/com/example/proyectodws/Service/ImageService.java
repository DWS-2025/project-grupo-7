package com.example.proyectodws.Service;
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
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.UUID;

@Service
public class ImageService {

    private static final Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");

    private static final String IMAGE_FOLDER = ".\\images";
    // Return a new path for a image
    private Path createFilePath(long imageId, Path folder) {

        return folder.resolve("image-" + imageId + ".jpg");
    }
    // Keep the image in a folder
    public void saveImage(MultipartFile image) throws IOException {
        Path directory = Paths.get(IMAGE_FOLDER);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        Path filePath = directory.resolve(image.getOriginalFilename());
        Files.write(filePath, image.getBytes());
    }

    // Return a image to the user
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

    public Blob filePathToBlob (String filePath) throws IOException, SQLException {
        byte[]bytes= Files.readAllBytes(Paths.get(filePath));
        Blob imageBlob = new SerialBlob(bytes);
        return imageBlob;
    }


    public static Blob convertToBlob(byte[] bytes) throws SQLException {
        return new SerialBlob(bytes);
    }


}
