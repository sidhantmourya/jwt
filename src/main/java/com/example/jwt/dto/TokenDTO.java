package com.example.jwt.dto;

public class TokenDTO {

    private String accessToken;
    private String tokenType;
    private long expires;
    private String refreshToken;
    long issuedAt;

    public TokenDTO(String accessToken, String tokenType, long expires, String refreshToken, long issuedAt) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expires = expires;
        this.refreshToken = refreshToken;
        this.issuedAt = issuedAt;
    }

    public TokenDTO() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(long issuedAt) {
        this.issuedAt = issuedAt;
    }
}
