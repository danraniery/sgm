package com.bomdestino.sgm.config.security.jwt;

import com.bomdestino.sgm.config.security.auth.SGMUserDetailsService;
import com.bomdestino.sgm.domain.User;
import com.bomdestino.sgm.exception.exceptions.ExpiredCredentialsException;
import com.bomdestino.sgm.exception.exceptions.NotFoundException;
import com.bomdestino.sgm.exception.exceptions.UserNotActivatedException;
import com.bomdestino.sgm.service.UserService;
import com.bomdestino.sgm.util.Translator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import static com.bomdestino.sgm.util.Constants.*;
import static com.bomdestino.sgm.util.TranslateConstants.*;

/**
 * Component used to handle the Spring Security authentication process.
 */
@Log4j2
@AllArgsConstructor
@Component
public class TokenProvider implements InitializingBean {

    private Key key;
    private final JwtConfig jwtConfig;
    private final Translator translator;
    private final UserService userService;
    private final SGMUserDetailsService userDetailsService;

    /**
     * Configure the key from a base 64 secret.
     */
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getBase64Secret());
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate an access token from the Spring Security {@link Authentication}.
     *
     * @param authentication     it's the authentication object created by Spring Security.
     * @return the user token that has been created.
     */
    public String createAccessToken(Authentication authentication) {
        String authoritiesAsString = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date validity = new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpirationInMinutes() * 60 * 1000);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authoritiesAsString)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /**
     * Generate an access token from the refresh token.
     *
     * @param refreshToken it's the user refresh token that has been created by Spring Security.
     * @return the user token that has been created.
     */
    public String createAccessToken(String refreshToken) {
        Claims claims = resolveClaims(refreshToken);
        UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());

        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date validity = new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpirationInMinutes() * 60 * 1000);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /**
     * Generate a refresh token from the Spring Security {@link Authentication}.
     *
     * @param authentication     it's the authentication object created by Spring Security.
     * @return the user token that has been created.
     */
    public String createRefreshToken(Authentication authentication) {
        Date validity = new Date(System.currentTimeMillis() + jwtConfig.getRefreshTokenExpirationInMinutes() * 60 * 1000);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /**
     * Authenticate the account from its token.
     *
     * @param token it's the user token.
     * @return the {@link Authentication} created by the Spring Security.
     */
    public Authentication getAuthentication(String token) {
        Claims claims = resolveClaims(token);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, authorities);
    }

    /**
     * Validate the token.
     *
     * @param authToken it's the token to be validated.
     * @param response  it's the response object injected by Spring.
     * @return true if the access token is valid or false if the token is invalid.
     */
    public boolean validateAccessToken(String authToken, HttpServletResponse response) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            Claims claims = resolveClaims(authToken);
            return verifyActivatedUser(userService.getUserByUsername(claims.getSubject()), response);
        } catch (ExpiredJwtException exc) {
            exceptionHandler(response, new ExpiredCredentialsException(translator.translate(ACCOUNT_SESSION_EXPIRED)), HttpServletResponse.SC_FORBIDDEN);
            return false;
        } catch (JwtException | IllegalArgumentException exc) {
            exceptionHandler(response, new BadCredentialsException(translator.translate(ACCESS_DENIED_MESSAGE)), HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    /**
     * Validate the refresh token.
     *
     * @param authToken it's the token to be validated.
     * @param response  it's the response object injected by Spring.
     * @return true if the access token is valid or false if the token is invalid.
     */
    public boolean validateRefreshToken(String authToken, HttpServletResponse response) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException exc) {
            exceptionHandler(response, new ExpiredCredentialsException(translator.translate(ACCOUNT_SESSION_EXPIRED)), HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    /**
     * Verify if the user is activated.
     *
     * @param user     it's the user to be verified.
     * @param response it's the response object injected by Spring.
     * @return true if the user is activated or false if the user is not activated.
     */
    public boolean verifyActivatedUser(User user, HttpServletResponse response) {
        if (Boolean.FALSE.equals(user.isActivated())) {
            exceptionHandler(response, new UserNotActivatedException(translator.translate(USER_NOT_ACTIVATED)), HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }

    /**
     * Read the Authentication Token from the request header and returns it.
     *
     * @param request it's the request object injected by Spring.
     * @return the current request token or null if it doesn't exist.
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (Strings.isNotBlank(bearerToken) && bearerToken.startsWith(BEARER_TOKEN)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Wrhite the exception in the response object.
     *
     * @param response  it's the response object injected by Spring.
     * @param exception it's the exception to be mapped in the response object.
     * @param status    it's the error status code to be mapped in the response object.
     */
    public void exceptionHandler(HttpServletResponse response, Exception exception, int status) {
        try {
            response.setHeader(ERROR_KEY, exception.getMessage());
            response.setStatus(status);
            Map<String, String> error = new HashMap<>();
            error.put(DETAIL_KEY, exception.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        } catch (IOException ioException) {
            throw new BadCredentialsException(translator.translate(INVALID_REFRESH_TOKEN));
        }
    }

    /**
     * Read and map the token to {@link Claims}.
     *
     * @param token token to be mapped.
     * @return the token {@link Claims}
     */
    private Claims resolveClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
