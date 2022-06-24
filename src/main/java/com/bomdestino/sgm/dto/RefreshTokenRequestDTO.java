package com.bomdestino.sgm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

import static com.bomdestino.sgm.util.TranslateConstants.INVALID_REFRESH_TOKEN;

/**
 * A DTO class with the user refresh token.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequestDTO {

    @NotBlank(message = INVALID_REFRESH_TOKEN)
    private String refreshToken;

}
