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
import java.util.List;
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
     * Upload file for study group resource sharing
     * Any authenticated user can upload to their study groups
     */
    @PostMapping("/upload/studygroup")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadStudyGroupFile(@RequestParam("file") MultipartFile file,
                                                   @RequestParam Long groupId,
                                                   @RequestParam Long userId) {
        try {
            // Store file in study-groups directory
            String filePath = fileStorageService.storeStudyGroupFile(file, groupId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("filePath", filePath);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", fileStorageService.getFileSizeDisplay(file.getSize()));
            response.put("fileType", file.getContentType());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Failed to upload file: " + e.getMessage()));
        }
    }

    /**
     * Upload profile picture
     * Authenticated users can upload their own profile picture
     */
    @PostMapping("/upload/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file,
                                                   @RequestParam Long userId) {
        try {
            String filePath = fileStorageService.storeProfilePicture(file, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile picture uploaded successfully");
            response.put("filePath", filePath);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", fileStorageService.getFileSizeDisplay(file.getSize()));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Failed to upload profile picture: " + e.getMessage()));
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

    /**
     * Upload course material file
     * FACULTY (their own courses), ADMIN/SUPER_ADMIN (all courses)
     */
    @PostMapping("/upload/course-material")
    @PreAuthorize("hasRole('FACULTY') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> uploadCourseMaterial(@RequestParam("file") MultipartFile file,
                                                   @RequestParam Long courseId,
                                                   @RequestParam Long uploaderId,
                                                   @RequestParam(required = false) String title,
                                                   @RequestParam(required = false) String description) {
        try {
            Map<String, Object> materialInfo = fileStorageService.storeCourseMaterial(
                file, courseId, uploaderId, title, description);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Course material uploaded successfully");
            response.putAll(materialInfo);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Failed to upload course material: " + e.getMessage()));
        }
    }

    /**
     * Get all course materials for a course
     * Available to all authenticated users enrolled in the course
     */
    @GetMapping("/course/{courseId}/materials")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCourseMaterials(@PathVariable Long courseId) {
        try {
            List<Map<String, Object>> materials = fileStorageService.getCourseMaterials(courseId);
            return ResponseEntity.ok(materials);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Failed to get course materials: " + e.getMessage()));
        }
    }

    /**
     * Download course material
     */
    @GetMapping("/course/{courseId}/materials/{materialId}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadCourseMaterial(@PathVariable Long courseId,
                                                           @PathVariable String materialId) {
        try {
            Resource resource = fileStorageService.loadCourseMaterial(courseId, materialId);
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
     * Delete course material
     * FACULTY (their own materials), ADMIN/SUPER_ADMIN (all)
     */
    @DeleteMapping("/course/{courseId}/materials/{materialId}")
    @PreAuthorize("hasRole('FACULTY') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteCourseMaterial(@PathVariable Long courseId,
                                                   @PathVariable String materialId) {
        try {
            fileStorageService.deleteCourseMaterial(courseId, materialId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Course material deleted successfully"));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Failed to delete course material: " + e.getMessage()));
        }
    }
}
