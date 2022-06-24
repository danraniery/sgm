package com.bomdestino.sgm.exception.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * A custom exception to use when a not activated user trying to authenticate.
 */
public class UserNotActivatedException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public UserNotActivatedException(String message) {
        super(message);
    }

}
