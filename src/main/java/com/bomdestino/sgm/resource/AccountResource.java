package com.bomdestino.sgm.resource;

import com.bomdestino.sgm.domain.User;
import com.bomdestino.sgm.dto.AccountResponseDTO;
import com.bomdestino.sgm.dto.PasswordChangeRequestDTO;
import com.bomdestino.sgm.service.UserService;
import com.bomdestino.sgm.util.ResponseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.net.URI;

import static com.bomdestino.sgm.util.EndpointConstants.ACCOUNT_URL;
import static com.bomdestino.sgm.util.EndpointConstants.UPDATE_PASSWORD_URL;

/**
 * REST controller for managing the user Account.
 */
@Log4j2
@AllArgsConstructor
@RestController
public class AccountResource {

    private final UserService userService;

    /**
     * {@code GET  /account} : get the current logged user data.
     *
     * @return the current logged user.
     */
    @Transactional
    @GetMapping(ACCOUNT_URL)
    @Secured("IS_AUTHENTICATED_FULLY")
    public AccountResponseDTO getAccount() {
        return new AccountResponseDTO(userService.getLoggedUser());
    }

    /**
     * {@code PATCH  /account/change-password} : change the password of the current logged user.
     *
     * @param dto it's the dto containing the new password.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @PatchMapping(UPDATE_PASSWORD_URL)
    @Secured("IS_AUTHENTICATED_FULLY")
    public ResponseEntity<URI> changePassword(@RequestBody PasswordChangeRequestDTO dto) {
        User user = userService.validateAndUpdatePassword(dto);
        return ResponseEntity.ok().location(ResponseUtils.toURI(user.getId())).build();
    }

}
