package com.example.swip.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    /**
     * Secret key used for issuing JWT
     * 아마 JWT 암.복호화용 키가 아닐까 생각
     */
    private String secretKey;
}