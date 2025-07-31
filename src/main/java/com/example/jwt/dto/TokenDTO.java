package com.example.jwt.dto;

import java.util.Date;

public class TokenDTO {

    private String accessToken;
    private String tokenType;
    private long expires;
    private String refreshToken;
    Date issuedAt;

    public TokenDTO(String accessToken, String tokenType, long expires, String refreshToken, Date issuedAt) {
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

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }
}
