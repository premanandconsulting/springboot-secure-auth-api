package com.premanand.secureauth.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for refreshing access tokens.
 */
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
