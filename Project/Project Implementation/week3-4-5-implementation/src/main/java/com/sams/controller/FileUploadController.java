package com.sams.controller;

import com.sams.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for file upload and download operations
 * Handles assignment file submissions with proper security
 */
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Upload file for assignment submission
     * Only STUDENT can upload their own submission
     */
    @PostMapping("/upload/assignment")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> uploadAssignmentFile(@RequestParam("file") MultipartFile file,
                                                  @RequestParam Long studentId,
                                                  @RequestParam Long assignmentId) {
        try {
            String filePath = fileStorageService.storeFile(file, studentId, assignmentId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("filePath", filePath);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", fileStorageService.getFileSizeDisplay(file.getSize()));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Failed to upload file: " + e.getMessage()));
        }
    }

    /**
     * Download file
     * STUDENT (own files), FACULTY (enrolled students), ADMIN/SUPER_ADMIN (all)
     */
    @GetMapping("/download")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) {
        try {
            Resource resource = fileStorageService.loadFileAsResource(filePath);

            // extract filename from path
            String filename = resource.getFilename();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete file
     * STUDENT (own files), FACULTY (can delete student submissions), ADMIN/SUPER_ADMIN (all)
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteFile(@RequestParam String filePath) {
        try {
            fileStorageService.deleteFile(filePath);
            return ResponseEntity.ok(Map.of("success", true, "message", "File deleted successfully"));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Failed to delete file: " + e.getMessage()));
        }
    }

    /**
     * Delete all submission files for a student
     * FACULTY (their assignments), ADMIN/SUPER_ADMIN (all)
     */
    @DeleteMapping("/delete-submission")
    @PreAuthorize("hasRole('FACULTY') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteSubmissionFiles(@RequestParam Long assignmentId,
                                                   @RequestParam Long studentId) {
        try {
            fileStorageService.deleteSubmissionFiles(assignmentId, studentId);
            return ResponseEntity.ok(Map.of("success", true, "message", "All submission files deleted successfully"));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Failed to delete files: " + e.getMessage()));
        }
    }

    /**
     * Check if file exists
     */
    @GetMapping("/exists")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> fileExists(@RequestParam String filePath) {
        boolean exists = fileStorageService.fileExists(filePath);
        return ResponseEntity.ok(exists);
    }

    /**
     * Get file size
     */
    @GetMapping("/size")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFileSize(@RequestParam String filePath) {
        try {
            long sizeBytes = fileStorageService.getFileSize(filePath);
            String sizeDisplay = fileStorageService.getFileSizeDisplay(sizeBytes);

            return ResponseEntity.ok(Map.of(
                    "sizeBytes", sizeBytes,
                    "sizeDisplay", sizeDisplay
            ));
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
