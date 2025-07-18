package com.example.jwt.dto;

import java.time.LocalDateTime;

public class ResponseDTO {

    private String username;
    private String email;
    private String roles;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;

    public ResponseDTO() {
    }

    public ResponseDTO(String username, String email, String roles, LocalDateTime createdAt, LocalDateTime lastUpdated) {
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
