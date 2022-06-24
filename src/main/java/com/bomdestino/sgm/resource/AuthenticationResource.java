package com.bomdestino.sgm.resource;

import com.bomdestino.sgm.dto.LoginRequestDTO;
import com.bomdestino.sgm.dto.LoginResponseDTO;
import com.bomdestino.sgm.dto.RefreshTokenRequestDTO;
import com.bomdestino.sgm.exception.exceptions.BusinessRuleException;
import com.bomdestino.sgm.exception.exceptions.UnauthorizedCredentialsException;
import com.bomdestino.sgm.service.AuthenticationService;
import com.bomdestino.sgm.service.UserService;
import com.bomdestino.sgm.util.Translator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.bomdestino.sgm.util.EndpointConstants.AUTHENTICATE_URL;
import static com.bomdestino.sgm.util.EndpointConstants.REFRESH_AUTHENTICATE_URL;
import static com.bomdestino.sgm.util.TranslateConstants.BAD_CREDENTIALS_MESSAGE;
import static com.bomdestino.sgm.util.TranslateConstants.LOGON_ATTEMPTS_EXCEEDED_MESSAGE;

/**
 * REST controller for managing the Spring authentication process.
 */
@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping(AUTHENTICATE_URL)
public class AuthenticationResource {

    private final Translator translator;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    /**
     * {@code POST  /authenticate} : authenticate the user credentials to create a new security token.
     *
     * @param dto it's the authentication object with username and password.
     * @return the {@link ResponseEntity<LoginResponseDTO>} with the new user token.
     * @throws BusinessRuleException            if the user exceeds the number of authentication attempts.
     * @throws UnauthorizedCredentialsException if the user credentials are invalid.
     */
    @PostMapping
    public ResponseEntity<LoginResponseDTO> authorize(@Valid @RequestBody LoginRequestDTO dto) {
        try {
            return this.authenticationService.authorize(dto);
        } catch (BadCredentialsException badCredentialsEx) {
            if (Boolean.TRUE.equals(userService.registerLogonAttemptWithBadCredentials(dto.getUsername()))) {
                throw new BusinessRuleException(translator.translate(LOGON_ATTEMPTS_EXCEEDED_MESSAGE));
            } else {
                throw new UnauthorizedCredentialsException(translator.translate(BAD_CREDENTIALS_MESSAGE));
            }
        }
    }

    /**
     * {@code POST  /authenticate/refresh} : create new security tokens based on provided refresh token.
     *
     * @param dto      it's the dto containing the refresh token.
     * @param response it's the response object injected by Spring.
     * @return the {@link ResponseEntity<LoginResponseDTO>} with the new security tokens.
     */
    @PostMapping(REFRESH_AUTHENTICATE_URL)
    public ResponseEntity<LoginResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO dto, HttpServletResponse response) {
        return this.authenticationService.refreshToken(dto, response);
    }

}
