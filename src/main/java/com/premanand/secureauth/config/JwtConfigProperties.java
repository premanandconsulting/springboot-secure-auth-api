package com.premanand.secureauth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class JwtConfigProperties {

    private String secret;
    private long accessTokenExpiry;
    private long refreshTokenExpiry;
    private String issuer;

}
