package com.jpbazaar.service.storage;

import com.jpbazaar.exception.InvalidRequestException;
import com.jpbazaar.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class LocalStorageServiceImpl implements ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalStorageServiceImpl.class);

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final Path rootLocation;

    public LocalStorageServiceImpl(@Value("${app.upload.dir:uploads/images}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            log.info("Initialized local image storage directory at: {}", rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize image storage directory at: " + rootLocation, e);
        }
    }

    @Override
    public String upload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("Cannot upload an empty file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidRequestException("Invalid image type: " + contentType + ". Allowed types: JPEG, PNG, WEBP, GIF");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "image.jpg"));
        if (originalFilename.contains("..")) {
            throw new InvalidRequestException("Filename contains invalid relative path sequence: " + originalFilename);
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        String uniqueFilename = UUID.randomUUID() + extension;
        String relativePath = uniqueFilename;

        try {
            Path targetDirectory = rootLocation;
            if (StringUtils.hasText(folder)) {
                targetDirectory = rootLocation.resolve(folder).normalize();
                if (!targetDirectory.startsWith(rootLocation)) {
                    throw new InvalidRequestException("Invalid target directory path");
                }
                Files.createDirectories(targetDirectory);
                relativePath = folder.replace('\\', '/') + "/" + uniqueFilename;
            }

            Path destinationPath = targetDirectory.resolve(uniqueFilename).normalize();
            if (!destinationPath.startsWith(rootLocation)) {
                throw new InvalidRequestException("Cannot store file outside current storage directory");
            }

            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Successfully stored image at: {}", destinationPath);

            return "/api/images/" + relativePath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + originalFilename, e);
        }
    }

    @Override
    public void delete(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            return;
        }

        // External URLs (e.g. http:// or https://) are remote CDN URLs not stored on local disk
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return;
        }

        try {
            String relativePath = imageUrl;
            if (imageUrl.startsWith("/api/images/")) {
                relativePath = imageUrl.substring("/api/images/".length());
            } else if (imageUrl.contains("/api/images/")) {
                relativePath = imageUrl.substring(imageUrl.indexOf("/api/images/") + "/api/images/".length());
            }

            Path filePath = rootLocation.resolve(relativePath).normalize();
            if (filePath.startsWith(rootLocation) && Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Deleted image from disk: {}", filePath);
            }
        } catch (Exception e) {
            log.warn("Failed to delete local image file: {}", imageUrl, e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            String sanitizedFilename = StringUtils.cleanPath(filename);
            if (sanitizedFilename.contains("..")) {
                throw new InvalidRequestException("Invalid file path sequence");
            }

            Path file = rootLocation.resolve(sanitizedFilename).normalize();
            if (!file.startsWith(rootLocation)) {
                throw new InvalidRequestException("Access to path outside storage directory is forbidden");
            }

            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Image file not found: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Image file not found: " + filename);
        }
    }

    @Override
    public String getContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) {
            return "image/png";
        } else if (lower.endsWith(".webp")) {
            return "image/webp";
        } else if (lower.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "image/jpeg";
        }
    }
}
