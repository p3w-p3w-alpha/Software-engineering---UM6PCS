package com.sams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// request DTO for sending a message to a study group
public class GroupMessageRequest {

    @NotBlank(message = "Message content is required")
    @Size(max = 2000, message = "Message cannot exceed 2000 characters")
    private String content;

    private String messageType = "TEXT"; // TEXT, FILE, IMAGE, LINK

    private String fileUrl; // for file/image messages

    private String fileName; // for file messages

    // constructors
    public GroupMessageRequest() {
    }

    public GroupMessageRequest(String content, String messageType) {
        this.content = content;
        this.messageType = messageType;
    }

    // getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
