package com.example.proyectodws.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class MediaService {

    private static final Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");
    private static final Path STATIC_IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "images");
    private static final Path UPLOADS_FOLDER = Paths.get(System.getProperty("user.dir"), "uploads");

    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final List<String> ALLOWED_VIDEO_EXTENSIONS = Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "webm");

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024; // 50MB

    // clean input
    private String secureCleanInput(String input) {
        if (!StringUtils.hasText(input)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Input cannot be empty");
        }

        String cleaned = input;
        for (int i = 0; i < 3; i++) {
            try {
                String previous = cleaned;
                cleaned = URLDecoder.decode(cleaned, "UTF-8");
                if (cleaned.equals(previous)) break;
            } catch (Exception e) {
                break;
            }
        }

        cleaned = Normalizer.normalize(cleaned, Normalizer.Form.NFKC);

        cleaned = cleaned.replaceAll("\\.\\.", "")
                .replaceAll("~/", "")
                .replaceAll("%", "")
                .replaceAll(":", "")
                .replaceAll("<", "")
                .replaceAll(">", "")
                .replaceAll("\\|", "")
                .replaceAll("\\*", "")
                .replaceAll("\\?", "")
                .replaceAll("\"", "")
                .replaceAll("'", "")
                .replaceAll("\\x00", "")
                .trim();

        if (!cleaned.matches("^[a-zA-Z0-9._/\\\\-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid characters in input");
        }

        if (cleaned.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Input too long");
        }

        return cleaned;
    }

    private String secureCleanResourcePath(String input) {
        if (!StringUtils.hasText(input)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource path cannot be empty");
        }

        String cleaned = input;
        for (int i = 0; i < 3; i++) {
            try {
                String previous = cleaned;
                cleaned = URLDecoder.decode(cleaned, "UTF-8");
                if (cleaned.equals(previous)) break;
            } catch (Exception e) {
                break;
            }
        }

        cleaned = Normalizer.normalize(cleaned, Normalizer.Form.NFKC);

        cleaned = cleaned.replace('\\', '/');

        String[] dangerousPatterns = {
                "..", "~/", "//",
                "....//", "....", "....\\\\",
                "%2e", "%2f", "%5c",
                "%252e", "%252f", "%255c",
                "%c0%2e", "%c0%2f", "%c0%5c",
                "\\u002e\\u002e", "\\u002f", "\\u005c",
                "file:", "http:", "ftp:",
                "etc/", "passwd", "boot.ini", "win.ini"
        };

        String lowerCleaned = cleaned.toLowerCase();
        for (String pattern : dangerousPatterns) {
            if (lowerCleaned.contains(pattern.toLowerCase())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Dangerous pattern detected in resource path: " + pattern);
            }
        }

        if (cleaned.startsWith("/") || cleaned.startsWith("\\") || cleaned.contains(":")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Absolute paths not allowed");
        }

        if (!cleaned.matches("^[a-zA-Z0-9/_.-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Resource path contains invalid characters after normalization");
        }

        try {
            Path testPath = Paths.get(cleaned).normalize();
            String normalizedStr = testPath.toString().replace('\\', '/');
            if (normalizedStr.contains("..") || !normalizedStr.equals(cleaned)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Path normalization changed the path - potential traversal");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path structure");
        }

        return cleaned;
    }

    private String validateAndGetExtension(String filename, List<String> allowedExtensions) {
        if (!StringUtils.hasText(filename)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename cannot be empty");
        }

        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must have an extension");
        }

        String extension = filename.substring(lastDot + 1).toLowerCase();
        if (!allowedExtensions.contains(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid file type. Allowed: " + String.join(", ", allowedExtensions));
        }

        return extension;
    }

    private void validateFileSize(MultipartFile file, long maxSize) {
        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "File too large. Max size: " + (maxSize / 1024 / 1024) + "MB");
        }
    }

    private void validateSecurePath(Path requestedPath, Path allowedBaseDirectory) {
        try {
            Path normalizedBase = allowedBaseDirectory.toAbsolutePath().normalize();
            Path normalizedRequested = requestedPath.toAbsolutePath().normalize();

            if (!normalizedRequested.startsWith(normalizedBase)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access denied");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path");
        }
    }

    private String generateSecureFilename(String originalFilename, List<String> allowedExtensions) {
        String extension = validateAndGetExtension(originalFilename, allowedExtensions);
        return UUID.randomUUID().toString() + "." + extension;
    }


    private Path createFilePath(long imageId, Path folder) {
        return folder.resolve("image-" + imageId + ".jpg");
    }

    public String saveImage(MultipartFile image) throws IOException {
        validateFileSize(image, MAX_IMAGE_SIZE);
        String secureFilename = generateSecureFilename(image.getOriginalFilename(), ALLOWED_IMAGE_EXTENSIONS);

        // create directory
        if (!Files.exists(STATIC_IMAGES_FOLDER)) {
            Files.createDirectories(STATIC_IMAGES_FOLDER);
        }

        Path filePath = STATIC_IMAGES_FOLDER.resolve(secureFilename);
        validateSecurePath(filePath, STATIC_IMAGES_FOLDER);

        Files.write(filePath, image.getBytes());

        return "/images/" + secureFilename;
    }

    public String saveImage(Blob image) throws IOException, SQLException {
        if (!Files.exists(STATIC_IMAGES_FOLDER)) {
            Files.createDirectories(STATIC_IMAGES_FOLDER);
        }

        String secureFilename = UUID.randomUUID().toString() + ".jpg";
        Path filePath = STATIC_IMAGES_FOLDER.resolve(secureFilename);
        validateSecurePath(filePath, STATIC_IMAGES_FOLDER);

        Files.write(filePath, image.getBytes(1, (int) image.length()));

        return "/images/" + secureFilename;
    }

    public String saveVideo(Long courseId, MultipartFile video) throws IOException {
        if (courseId == null || courseId <= 0 || courseId > 999999) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid course ID");
        }

        validateFileSize(video, MAX_VIDEO_SIZE);
        String secureFilename = generateSecureFilename(video.getOriginalFilename(), ALLOWED_VIDEO_EXTENSIONS);

        // create directory
        Path videosBaseDir = UPLOADS_FOLDER.resolve("videos");
        Path courseDir = videosBaseDir.resolve(courseId.toString());

        validateSecurePath(courseDir, videosBaseDir);

        if (!Files.exists(courseDir)) {
            Files.createDirectories(courseDir);
        }

        Path filePath = courseDir.resolve(secureFilename);
        validateSecurePath(filePath, courseDir);

        Files.write(filePath, video.getBytes());

        return "/videos/" + courseId + "/" + secureFilename;
    }

    public ResponseEntity<Object> createResponseFromImage(String folderName, long imageId) throws MalformedURLException {
        String safeFolderName = secureCleanInput(folderName);

        Path folder = FILES_FOLDER.resolve(safeFolderName);
        validateSecurePath(folder, FILES_FOLDER);

        Path imagePath = createFilePath(imageId, folder);
        validateSecurePath(imagePath, FILES_FOLDER);

        if (!Files.exists(imagePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource file = new UrlResource(imagePath.toUri());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(file);
    }

    public void deleteImage(String folderName, long imageId) throws IOException {
        String safeFolderName = secureCleanInput(folderName);

        Path folder = FILES_FOLDER.resolve(safeFolderName);
        validateSecurePath(folder, FILES_FOLDER);

        Path imageFile = createFilePath(imageId, folder);
        validateSecurePath(imageFile, FILES_FOLDER);

        Files.deleteIfExists(imageFile);
    }

    public Resource getImage(String imageName) {
        String safeImageName = secureCleanInput(imageName);

        Path imagePath = FILES_FOLDER.resolve(safeImageName);
        validateSecurePath(imagePath, FILES_FOLDER);

        if (!Files.exists(imagePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        }

        try {
            return new UrlResource(imagePath.toUri());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot access image");
        }
    }

    public Resource getVideo(Long courseId, String videoName) {
        if (courseId == null || courseId <= 0 || courseId > 999999) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid course ID");
        }

        String safeVideoName = secureCleanInput(videoName);

        Path videosBaseDir = UPLOADS_FOLDER.resolve("videos");
        Path courseDir = videosBaseDir.resolve(courseId.toString());
        validateSecurePath(courseDir, videosBaseDir);

        Path videoPath = courseDir.resolve(safeVideoName);
        validateSecurePath(videoPath, courseDir);

        if (!Files.exists(videoPath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found");
        }

        try {
            return new UrlResource(videoPath.toUri());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot access video");
        }
    }

    public String createImage(MultipartFile multiPartFile) {
        validateFileSize(multiPartFile, MAX_IMAGE_SIZE);
        String secureFilename = generateSecureFilename(multiPartFile.getOriginalFilename(), ALLOWED_IMAGE_EXTENSIONS);

        Path imagePath = FILES_FOLDER.resolve(secureFilename);
        validateSecurePath(imagePath, FILES_FOLDER);

        try {
            multiPartFile.transferTo(imagePath);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot save image", ex);
        }

        return secureFilename;
    }

    public Blob filePathToBlob(String resourcePath) throws IOException, SQLException {
        String cleanPath = secureCleanResourcePath(resourcePath);

        try {
            ClassPathResource resource = new ClassPathResource(cleanPath);
            if (!resource.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found: " + cleanPath);
            }

            try (InputStream is = resource.getInputStream()) {
                byte[] bytes = is.readAllBytes();
                return new SerialBlob(bytes);
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading resource: " + e.getMessage());
        }
    }

    public static Blob convertToBlob(byte[] bytes) throws SQLException {
        return new SerialBlob(bytes);
    }
}