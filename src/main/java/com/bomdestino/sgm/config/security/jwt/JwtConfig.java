package com.bomdestino.sgm.config.security.jwt;

import com.google.common.net.HttpHeaders;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration of {@link JwtConfig} based on Spring.
 */
@ConfigurationProperties(prefix = "application.jwt")
@Component
@NoArgsConstructor
@Data
public class JwtConfig {

    private String secretKey;
    private String base64Secret;
    private String tokenPrefix;
    private Integer accessTokenExpirationInMinutes;
    private Integer refreshTokenExpirationInMinutes;

    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION;
    }

}
