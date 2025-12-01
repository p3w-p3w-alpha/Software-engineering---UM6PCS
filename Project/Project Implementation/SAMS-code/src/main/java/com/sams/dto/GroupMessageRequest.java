package com.sams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * request object for sending messages to study groups
 * frontend sends this when user posts a message in thier group chat
 */
public class GroupMessageRequest {

    // the actual message content
    @NotBlank(message = "Message content is required")
    @Size(max = 2000, message = "Message cannot exceed 2000 characters")
    private String content;

    // type of message being sent (TEXT, FILE, IMAGE, LINK)
    private String messageType = "TEXT";

    // url for file/image messages if applicable
    private String fileUrl;

    // filename for file attachments
    private String fileName;

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
