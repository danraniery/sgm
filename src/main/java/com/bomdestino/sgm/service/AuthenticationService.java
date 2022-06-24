package com.bomdestino.sgm.service;

import com.bomdestino.sgm.config.security.jwt.TokenProvider;
import com.bomdestino.sgm.dto.LoginRequestDTO;
import com.bomdestino.sgm.dto.LoginResponseDTO;
import com.bomdestino.sgm.dto.RefreshTokenRequestDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

import static com.bomdestino.sgm.util.Constants.AUTHORIZATION_HEADER;
import static com.bomdestino.sgm.util.Constants.BEARER_TOKEN;

/**
 * Service class for authentication management.
 */
@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    /**
     * Validate the user credentials and get a security token from it.
     *
     * @param dto it's the authentication object with username and password.
     * @return the {@link ResponseEntity< LoginResponseDTO >} with the new user token.
     */
    public ResponseEntity<LoginResponseDTO> authorize(LoginRequestDTO dto) {
        userService.verifyUserCredentials(dto.getUsername());
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        userService.resetBadLoginAttempts(dto.getUsername());
        return authorizeUser(authentication);
    }

    /**
     * Generate a new access token based on the provided refresh token.
     *
     * @param dto      it's the dto containing the refresh token.
     * @param response it's the response object injected by Spring.
     * @return the {@link LoginResponseDTO} with the new security tokens.
     */
    public ResponseEntity<LoginResponseDTO> refreshToken(RefreshTokenRequestDTO dto, HttpServletResponse response) {
        if (tokenProvider.validateRefreshToken(dto.getRefreshToken(), response)) {
            String accessToken = tokenProvider.createAccessToken(dto.getRefreshToken());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(AUTHORIZATION_HEADER, BEARER_TOKEN + accessToken);
            return new ResponseEntity<>(new LoginResponseDTO(accessToken, dto.getRefreshToken()), httpHeaders, HttpStatus.OK);
        }
        return null;
    }

    /**
     * Authorize the user and create a security token.
     *
     * @param authentication it's the user authentication object created by the Spring Security.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}, the access token and the refresh token.
     */
    private ResponseEntity<LoginResponseDTO> authorizeUser(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, BEARER_TOKEN + accessToken);
        return new ResponseEntity<>(new LoginResponseDTO(accessToken, refreshToken), httpHeaders, HttpStatus.OK);
    }

}
