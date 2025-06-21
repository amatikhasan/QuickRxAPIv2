package com.example.quickrx.service;

import com.example.quickrx.config.FileStorageProperties;
import com.example.quickrx.exception.FileStorageException; // Will create this
import com.example.quickrx.exception.ResourceNotFoundException; // Already exists
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file, String subDirectory) {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check for invalid characters
            if (originalFileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + originalFileName);
            }

            // Generate a unique file name to prevent collisions
            String fileExtension = "";
            try {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            } catch (Exception e) {
                // No extension or invalid filename
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            Path targetLocationDir = this.fileStorageLocation.resolve(subDirectory);
            Files.createDirectories(targetLocationDir); // Ensure subdirectory exists

            Path targetLocation = targetLocationDir.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return the path relative to the uploadDir/subDirectory for storage in DB
            // e.g., "category_images/uuid.jpg" or just "uuid.jpg" if subDirectory is part of the stored URL base
            // For simplicity, let's store "subDirectory/uniqueFileName"
            return Paths.get(subDirectory, uniqueFileName).toString().replace("\\", "/");

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String relativePath) {
        try {
            Path filePath = this.fileStorageLocation.resolve(relativePath).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found " + relativePath);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found " + relativePath, ex);
        }
    }

    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return; // No file to delete
        }
        try {
            Path filePath = this.fileStorageLocation.resolve(relativePath).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // Log this, but maybe don't throw an exception that rolls back a transaction
            // if deleting a file fails but the DB operation succeeded.
            // Or, handle this more robustly depending on requirements.
            System.err.println("Could not delete file: " + relativePath + " due to " + ex.getMessage());
            // For now, let's throw an exception to make it visible
            // throw new FileStorageException("Could not delete file " + relativePath + ". Please try again!", ex);
        }
    }
}
