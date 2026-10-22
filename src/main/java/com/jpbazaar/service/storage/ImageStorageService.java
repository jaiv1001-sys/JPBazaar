package com.jpbazaar.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Pluggable abstraction for image storage.
 * Designed to support Local Storage initially, with drop-in support for AWS S3 or Cloudinary.
 */
public interface ImageStorageService {

    /**
     * Upload an image file to the storage provider.
     *
     * @param file   The multipart file to upload
     * @param folder Destination sub-directory / folder key
     * @return Public or accessible URL/URI of the uploaded image
     */
    String upload(MultipartFile file, String folder);

    /**
     * Delete an image from storage using its URL or key.
     *
     * @param imageUrl The URL of the image to delete
     */
    void delete(String imageUrl);

    /**
     * Load a locally stored file as a Spring Resource for streaming.
     *
     * @param filename Sanitized name of the file
     * @return Spring Resource
     */
    Resource loadAsResource(String filename);

    /**
     * Determine the MIME Content-Type for a file.
     *
     * @param filename The filename
     * @return Content-Type string (e.g., "image/jpeg")
     */
    String getContentType(String filename);
}
