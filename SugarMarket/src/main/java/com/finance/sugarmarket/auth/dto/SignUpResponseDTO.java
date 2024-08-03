package com.finance.sugarmarket.auth.dto;

public class SignUpResponseDTO {

    private String username;
    private String emailId;
    private String message;

    public SignUpResponseDTO(String username, String emailId, String message) {
        this.username = username;
        this.emailId = emailId;
        this.message = message;
    }
    
    public SignUpResponseDTO(String message) {
    	this.message = message;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}