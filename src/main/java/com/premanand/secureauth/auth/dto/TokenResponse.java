package com.premanand.secureauth.auth.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // getters
}