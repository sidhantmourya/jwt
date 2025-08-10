package com.example.jwt.dto;

public class RequestLogoutDTO {
    private  String token;

    public RequestLogoutDTO() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
