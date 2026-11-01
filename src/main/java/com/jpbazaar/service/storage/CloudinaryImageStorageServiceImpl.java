package com.jpbazaar.service.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.exception.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
@Service
@ConditionalOnProperty(name = "cloudinary.enabled", havingValue = "true")
public class CloudinaryImageStorageServiceImpl implements ImageStorageService {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    public CloudinaryImageStorageServiceImpl(ObjectMapper objectMapper) {
        this.restClient = RestClient.builder().build();
        this.objectMapper = objectMapper;
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

        try {
            long timestamp = System.currentTimeMillis() / 1000L;
            String folderName = (folder != null && !folder.isBlank()) ? "jpbazaar/" + folder : "jpbazaar/products";

            // Cloudinary signature parameters must be alphabetically sorted
            Map<String, String> params = new TreeMap<>();
            params.put("folder", folderName);
            params.put("timestamp", String.valueOf(timestamp));

            StringBuilder toSign = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!toSign.isEmpty()) toSign.append("&");
                toSign.append(entry.getKey()).append("=").append(entry.getValue());
            }
            toSign.append(apiSecret);

            String signature = calculateSha1(toSign.toString());

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("api_key", apiKey);
            body.add("timestamp", String.valueOf(timestamp));
            body.add("folder", folderName);
            body.add("signature", signature);

            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.jpg";
                }
            };
            body.add("file", fileResource);

            String uploadUrl = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload";

            ResponseEntity<String> response = restClient.post()
                    .uri(uploadUrl)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String secureUrl = root.path("secure_url").asText();
                log.info("Successfully uploaded image to Cloudinary: {}", secureUrl);
                return secureUrl;
            } else {
                throw new RuntimeException("Cloudinary upload failed with status: " + response.getStatusCode());
            }
        } catch (Exception ex) {
            log.error("Cloudinary upload error: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to upload image to cloud storage: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void delete(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            return;
        }

        try {
            // Extract public_id from Cloudinary URL
            // Format: .../upload/v12345/jpbazaar/products/sample.jpg
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex == -1) return;

            String pathAfterUpload = imageUrl.substring(uploadIndex + "/upload/".length());
            // Skip version if present (e.g. v123456789/)
            if (pathAfterUpload.startsWith("v") && pathAfterUpload.indexOf('/') != -1) {
                pathAfterUpload = pathAfterUpload.substring(pathAfterUpload.indexOf('/') + 1);
            }
            int dotIndex = pathAfterUpload.lastIndexOf('.');
            String publicId = (dotIndex != -1) ? pathAfterUpload.substring(0, dotIndex) : pathAfterUpload;

            long timestamp = System.currentTimeMillis() / 1000L;
            String toSign = "public_id=" + publicId + "&timestamp=" + timestamp + apiSecret;
            String signature = calculateSha1(toSign);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("public_id", publicId);
            body.add("api_key", apiKey);
            body.add("timestamp", String.valueOf(timestamp));
            body.add("signature", signature);

            String destroyUrl = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/destroy";
            restClient.post()
                    .uri(destroyUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully deleted image from Cloudinary: {}", publicId);
        } catch (Exception ex) {
            log.warn("Failed to delete image from Cloudinary: {}", ex.getMessage());
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            URI uri = URI.create(filename);
            return new UrlResource(uri);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load remote image resource: " + filename, ex);
        }
    }

    @Override
    public String getContentType(String filename) {
        if (filename == null) return "image/jpeg";
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".gif")) return "image/gif";
        return "image/jpeg";
    }

    private String calculateSha1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not available", e);
        }
    }
}
