package com.sams.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

// service for handling file storage operations on local file system
// stores assignment submission files in a structured directory format
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

    // delete all files for a specific student's submission
    public void deleteSubmissionFiles(Long assignmentId, Long studentId) throws IOException {
        Path studentPath = Paths.get(uploadDirectory, "assignments",
                assignmentId.toString(), "student_" + studentId);

        if (Files.exists(studentPath)) {
            // delete all files in the student directory
            Files.walk(studentPath)
                    .sorted((a, b) -> b.compareTo(a)) // reverse order to delete files before directories
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // log error but continue
                            System.err.println("Failed to delete: " + path);
                        }
                    });
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
}
