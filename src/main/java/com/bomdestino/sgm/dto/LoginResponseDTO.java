package com.bomdestino.sgm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A respons DTO with the user acces token and refresh token.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private String accessToken;
    private String refreshToken;

}
