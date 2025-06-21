package com.example.quickrx.controller;

import com.example.quickrx.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/files") // Or a more generic path like "/uploads" or "/media"
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/{mainDir}/{subDirOrFile}/{fileName:.+}") // e.g., /api/files/disease_images/123/uuid.jpg
    public ResponseEntity<Resource> downloadFile(@PathVariable String mainDir,
                                                 @PathVariable String subDirOrFile,
                                                 @PathVariable String fileName,
                                                 HttpServletRequest request) {
        // Construct the relative path carefully
        String relativePath = mainDir + "/" + subDirOrFile + "/" + fileName;
        return serveFile(relativePath, request);
    }

    @GetMapping("/{mainDir}/{fileName:.+}") // e.g., /api/files/category_images/uuid.jpg
    public ResponseEntity<Resource> downloadFileSimple(@PathVariable String mainDir,
                                                 @PathVariable String fileName,
                                                 HttpServletRequest request) {
        String relativePath = mainDir + "/" + fileName;
        return serveFile(relativePath, request);
    }


    private ResponseEntity<Resource> serveFile(String relativePath, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(relativePath);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type for path: " + relativePath);
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                // Use Content-Disposition "inline" to display if browser supports, "attachment" to force download
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
