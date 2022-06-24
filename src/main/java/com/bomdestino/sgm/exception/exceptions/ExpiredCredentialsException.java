package com.bomdestino.sgm.exception.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A custom exception to use when a valid user must update their password.
 */
@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class ExpiredCredentialsException extends RuntimeException {

    public ExpiredCredentialsException(String message) {
        super(message);
    }

}
