package com.sams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * request object for sending private messages
 * frontend sends this when users want to send direct messages to each other
 */
public class PrivateMessageRequest {

    // message content to send
    @NotBlank(message = "Message content is required")
    @Size(max = 2000, message = "Message cannot exceed 2000 characters")
    private String content;

    // constructors
    public PrivateMessageRequest() {
    }

    public PrivateMessageRequest(String content) {
        this.content = content;
    }

    // getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
