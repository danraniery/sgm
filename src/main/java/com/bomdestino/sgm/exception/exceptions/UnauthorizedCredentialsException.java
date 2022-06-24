package com.bomdestino.sgm.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A custom exception to use when to happen any error because of the user credentials.
 */
@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedCredentialsException extends RuntimeException {

    public UnauthorizedCredentialsException(String message) {
        super(message);
    }

}
