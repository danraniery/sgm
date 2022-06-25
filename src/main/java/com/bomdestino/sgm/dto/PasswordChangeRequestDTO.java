package com.bomdestino.sgm.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.bomdestino.sgm.util.TranslateConstants.*;

/**
 * A DTO representing a password change request.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PasswordChangeRequestDTO {

    @NotBlank(message = USER_PASSWORD_BLANK)
    @Size(min = 7, message = USER_PASSWORD_SIZE)
    private String password;

    @NotBlank(message = USER_CONFIRM_PASSWORD_BLANK)
    @Size(min = 7, message = USER_CONFIRM_PASSWORD_SIZE)
    private String confirmPassword;

}
