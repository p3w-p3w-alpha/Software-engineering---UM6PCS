package com.sams.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * service for handling file storage operations on local file system
 * stores assignment submission files in a structured directory format
 *
 * handles file uploads for: assignments, study groups, profiles, course materials
 * validates file types and sizes before storing
 *
 * TODO: might wanna move to cloud storage (S3 or something) later for scalability
 * this works for now tho
 */
@Service
public class FileStorageService {

    @Value("${file.upload.directory:./uploads}")
    private String uploadDirectory;

    @Value("${assignment.allowed.file.types:pdf,docx,doc,txt,zip,java,py,cpp,c,js,html,css}")
    private String allowedFileTypes;

    @Value("${assignment.max.file.size.mb:10}")
    private int maxFileSizeMb;

    // store a file for an assignment submission
    public String storeFile(MultipartFile file, Long studentId, Long assignmentId) throws IOException {
        // validate file is not empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        // get original filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        // chekc for invalid characters in filename
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Filename contains invalid path sequence: " + originalFilename);
        }

        // validate file type
        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedFileType(fileExtension)) {
            throw new IllegalArgumentException("File type not allowed: " + fileExtension +
                    ". Allowed types: " + allowedFileTypes);
        }

        // validate file size
        long fileSizeBytes = file.getSize();
        long maxSizeBytes = (long) maxFileSizeMb * 1024 * 1024;
        if (fileSizeBytes > maxSizeBytes) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " +
                    maxFileSizeMb + "MB");
        }

        // generate unique filename to avoid conflicts
        String uniqueFilename = generateUniqueFilename(originalFilename, studentId);

        // create directory structure: uploads/assignments/{assignmentId}/student_{studentId}/
        Path assignmentPath = Paths.get(uploadDirectory, "assignments", assignmentId.toString(),
                "student_" + studentId);

        try {
            // create directories if they don't exist
            Files.createDirectories(assignmentPath);

            // resolve the full file path
            Path targetLocation = assignmentPath.resolve(uniqueFilename);

            // copy file to target location (replace existing file if it exists)
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // return relative path for storing in database
            return Paths.get("assignments", assignmentId.toString(),
                    "student_" + studentId, uniqueFilename).toString();

        } catch (IOException ex) {
            throw new IOException("Could not store file " + uniqueFilename + ". Please try again!", ex);
        }
    }

    // load a file as a resource for download
    public Resource loadFileAsResource(String filePath) throws IOException {
        try {
            Path fullPath = Paths.get(uploadDirectory).resolve(filePath).normalize();
            Resource resource = new UrlResource(fullPath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("File not found or not readable: " + filePath);
            }
        } catch (Exception ex) {
            throw new IOException("File not found: " + filePath, ex);
        }
    }

    // delete a file from the file system
    public void deleteFile(String filePath) throws IOException {
        try {
            Path fullPath = Paths.get(uploadDirectory).resolve(filePath).normalize();

            // chekc if file exists
            if (!Files.exists(fullPath)) {
                throw new IOException("File not found: " + filePath);
            }

            // delete the file
            Files.delete(fullPath);

            // optionally, clean up empty parent directories
            cleanupEmptyDirectories(fullPath.getParent());

        } catch (IOException ex) {
            throw new IOException("Could not delete file: " + filePath, ex);
        }
    }

    // store a file for a study group resource
    public String storeStudyGroupFile(MultipartFile file, Long groupId, Long userId) throws IOException {
        // validate file is not empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        // get original filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        // check for invalid characters in filename
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Filename contains invalid path sequence: " + originalFilename);
        }

        // validate file type (allow PDF and common document types)
        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedFileType(fileExtension)) {
            throw new IllegalArgumentException("File type not allowed: " + fileExtension +
                    ". Allowed types: " + allowedFileTypes);
        }

        // validate file size
        long fileSizeBytes = file.getSize();
        long maxSizeBytes = (long) maxFileSizeMb * 1024 * 1024;
        if (fileSizeBytes > maxSizeBytes) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " +
                    maxFileSizeMb + "MB");
        }

        // generate unique filename
        String uniqueFilename = generateUniqueFilename(originalFilename, userId);

        // create directory structure: uploads/study-groups/{groupId}/
        Path studyGroupPath = Paths.get(uploadDirectory, "study-groups", groupId.toString());

        try {
            // create directories if they don't exist
            Files.createDirectories(studyGroupPath);

            // resolve the full file path
            Path targetLocation = studyGroupPath.resolve(uniqueFilename);

            // copy file to target location (replace existing file if it exists)
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // return relative path for storing in database
            return Paths.get("study-groups", groupId.toString(), uniqueFilename).toString();

        } catch (IOException ex) {
            throw new IOException("Could not store file " + uniqueFilename + ". Please try again!", ex);
        }
    }

    // store a profile picture for a user
    public String storeProfilePicture(MultipartFile file, Long userId) throws IOException {
        // validate file is not empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        // get original filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        // check for invalid characters in filename
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Filename contains invalid path sequence: " + originalFilename);
        }

        // validate image file type
        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        List<String> allowedImageTypes = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
        if (!allowedImageTypes.contains(fileExtension)) {
            throw new IllegalArgumentException("Invalid image type: " + fileExtension +
                    ". Allowed types: jpg, jpeg, png, gif, webp");
        }

        // validate file size (max 2MB for profile pictures)
        long fileSizeBytes = file.getSize();
        long maxSizeBytes = 2 * 1024 * 1024; // 2MB
        if (fileSizeBytes > maxSizeBytes) {
            throw new IllegalArgumentException("Profile picture exceeds maximum size of 2MB");
        }

        // use a consistent filename for profile picture (profile.{ext})
        String profileFilename = "profile_" + userId + "." + fileExtension;

        // create directory structure: uploads/profiles/{userId}/
        Path profilePath = Paths.get(uploadDirectory, "profiles", userId.toString());

        try {
            // create directories if they don't exist
            Files.createDirectories(profilePath);

            // resolve the full file path
            Path targetLocation = profilePath.resolve(profileFilename);

            // copy file to target location (replace existing profile picture)
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // return relative path for storing in database
            return Paths.get("profiles", userId.toString(), profileFilename).toString();

        } catch (IOException ex) {
            throw new IOException("Could not store profile picture. Please try again!", ex);
        }
    }

    // delete all files for a specific student's submission
    // FIXME: this could fail if directory doesnt exist - should handle that better
    public void deleteSubmissionFiles(Long assignmentId, Long studentId) throws IOException {
        Path studentPath = Paths.get(uploadDirectory, "assignments",
                assignmentId.toString(), "student_" + studentId);

        if (Files.exists(studentPath)) {
            // delete all files in the student directory using try-with-resources to prevent resource leak
            try (var pathStream = Files.walk(studentPath)) {
                pathStream
                    .sorted((a, b) -> b.compareTo(a)) // reverse order to delete files before directories
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // log error but continue - use proper logging in production
                        }
                    });
            }
        }
    }

    // get file extension from filename
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    // chekc if file type is allowed
    private boolean isAllowedFileType(String fileExtension) {
        if (fileExtension == null || fileExtension.isEmpty()) {
            return false;
        }

        List<String> allowed = Arrays.asList(allowedFileTypes.toLowerCase().split(","));
        return allowed.contains(fileExtension.toLowerCase().trim());
    }

    // generate a unique filename using timestamp and student ID
    private String generateUniqueFilename(String originalFilename, Long studentId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = getFileExtension(originalFilename);
        String nameWithoutExtension = originalFilename;

        if (!extension.isEmpty()) {
            nameWithoutExtension = originalFilename.substring(0,
                    originalFilename.length() - extension.length() - 1);
        }

        // sanitize filename - remove special characters
        nameWithoutExtension = nameWithoutExtension.replaceAll("[^a-zA-Z0-9_-]", "_");

        // construct unique filename: {name}_{studentId}_{timestamp}.{ext}
        if (!extension.isEmpty()) {
            return nameWithoutExtension + "_" + studentId + "_" + timestamp + "." + extension;
        } else {
            return nameWithoutExtension + "_" + studentId + "_" + timestamp;
        }
    }

    // clean up empty directories after file deletion
    private void cleanupEmptyDirectories(Path directory) {
        try {
            // only clean up if directory is empty and not the root upload directory
            Path rootPath = Paths.get(uploadDirectory).normalize();

            while (directory != null &&
                   !directory.equals(rootPath) &&
                   Files.isDirectory(directory) &&
                   isEmpty(directory)) {

                Files.delete(directory);
                directory = directory.getParent();
            }
        } catch (IOException ex) {
            // ignore cleanup errors - not critical
        }
    }

    // chekc if directory is empty
    private boolean isEmpty(Path directory) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }

    // get file size in human-readable format
    public String getFileSizeDisplay(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }

    // validate if file exists
    public boolean fileExists(String filePath) {
        try {
            Path fullPath = Paths.get(uploadDirectory).resolve(filePath).normalize();
            return Files.exists(fullPath);
        } catch (Exception ex) {
            return false;
        }
    }

    // get file size in bytes
    public long getFileSize(String filePath) throws IOException {
        Path fullPath = Paths.get(uploadDirectory).resolve(filePath).normalize();
        return Files.size(fullPath);
    }

    // store a course material file
    public Map<String, Object> storeCourseMaterial(MultipartFile file, Long courseId, Long uploaderId,
                                                    String title, String description) throws IOException {
        // validate file is not empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        // get original filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        // check for invalid characters in filename
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Filename contains invalid path sequence: " + originalFilename);
        }

        // validate file type
        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedFileType(fileExtension)) {
            throw new IllegalArgumentException("File type not allowed: " + fileExtension +
                    ". Allowed types: " + allowedFileTypes);
        }

        // validate file size
        long fileSizeBytes = file.getSize();
        long maxSizeBytes = (long) maxFileSizeMb * 1024 * 1024;
        if (fileSizeBytes > maxSizeBytes) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " +
                    maxFileSizeMb + "MB");
        }

        // generate unique material ID
        String materialId = UUID.randomUUID().toString().substring(0, 8);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFilename = materialId + "_" + timestamp + "_" + originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        // create directory structure: uploads/courses/{courseId}/materials/
        Path courseMaterialsPath = Paths.get(uploadDirectory, "courses", courseId.toString(), "materials");

        try {
            // create directories if they don't exist
            Files.createDirectories(courseMaterialsPath);

            // resolve the full file path
            Path targetLocation = courseMaterialsPath.resolve(uniqueFilename);

            // copy file to target location
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // return material info
            Map<String, Object> materialInfo = new HashMap<>();
            materialInfo.put("materialId", materialId);
            materialInfo.put("filePath", Paths.get("courses", courseId.toString(), "materials", uniqueFilename).toString());
            materialInfo.put("fileName", originalFilename);
            materialInfo.put("fileSize", getFileSizeDisplay(fileSizeBytes));
            materialInfo.put("fileSizeBytes", fileSizeBytes);
            materialInfo.put("fileType", fileExtension);
            materialInfo.put("title", title != null ? title : originalFilename);
            materialInfo.put("description", description);
            materialInfo.put("uploadedBy", uploaderId);
            materialInfo.put("uploadedAt", LocalDateTime.now().toString());
            materialInfo.put("courseId", courseId);

            return materialInfo;

        } catch (IOException ex) {
            throw new IOException("Could not store course material. Please try again!", ex);
        }
    }

    // get all course materials for a course
    public List<Map<String, Object>> getCourseMaterials(Long courseId) throws IOException {
        Path courseMaterialsPath = Paths.get(uploadDirectory, "courses", courseId.toString(), "materials");
        List<Map<String, Object>> materials = new ArrayList<>();

        if (!Files.exists(courseMaterialsPath)) {
            return materials;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(courseMaterialsPath)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    String filename = entry.getFileName().toString();
                    // Extract material ID from filename (first part before underscore)
                    String materialId = filename.split("_")[0];

                    BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);

                    Map<String, Object> materialInfo = new HashMap<>();
                    materialInfo.put("materialId", materialId);
                    materialInfo.put("fileName", extractOriginalFilename(filename));
                    materialInfo.put("fullFileName", filename);
                    materialInfo.put("filePath", Paths.get("courses", courseId.toString(), "materials", filename).toString());
                    materialInfo.put("fileSize", getFileSizeDisplay(attrs.size()));
                    materialInfo.put("fileSizeBytes", attrs.size());
                    materialInfo.put("fileType", getFileExtension(filename));
                    materialInfo.put("uploadedAt", attrs.creationTime().toString());
                    materialInfo.put("courseId", courseId);

                    materials.add(materialInfo);
                }
            }
        }

        // Sort by upload date (newest first)
        materials.sort((a, b) -> ((String) b.get("uploadedAt")).compareTo((String) a.get("uploadedAt")));

        return materials;
    }

    // extract original filename from stored filename format: {materialId}_{timestamp}_{originalName}
    private String extractOriginalFilename(String storedFilename) {
        String[] parts = storedFilename.split("_", 3);
        if (parts.length >= 3) {
            return parts[2];
        }
        return storedFilename;
    }

    // load a course material file as a resource
    public Resource loadCourseMaterial(Long courseId, String materialId) throws IOException {
        Path courseMaterialsPath = Paths.get(uploadDirectory, "courses", courseId.toString(), "materials");

        if (!Files.exists(courseMaterialsPath)) {
            throw new IOException("Course materials directory not found");
        }

        // Find the file that starts with the materialId
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(courseMaterialsPath, materialId + "_*")) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    Resource resource = new UrlResource(entry.toUri());
                    if (resource.exists() && resource.isReadable()) {
                        return resource;
                    }
                }
            }
        }

        throw new IOException("Course material not found: " + materialId);
    }

    // delete a course material
    public void deleteCourseMaterial(Long courseId, String materialId) throws IOException {
        Path courseMaterialsPath = Paths.get(uploadDirectory, "courses", courseId.toString(), "materials");

        if (!Files.exists(courseMaterialsPath)) {
            throw new IOException("Course materials directory not found");
        }

        // Find and delete the file that starts with the materialId
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(courseMaterialsPath, materialId + "_*")) {
            boolean found = false;
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    Files.delete(entry);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IOException("Course material not found: " + materialId);
            }
        }

        // Clean up empty directories
        cleanupEmptyDirectories(courseMaterialsPath);
    }
}
