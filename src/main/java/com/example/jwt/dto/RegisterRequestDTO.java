package com.example.jwt.dto;

public class RegisterRequestDTO {

    private String username;
    private String email;
    private String roles;
    private String password;

    public RegisterRequestDTO(String username, String email, String roles, String password) {
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.password = password;
    }

    public RegisterRequestDTO() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
