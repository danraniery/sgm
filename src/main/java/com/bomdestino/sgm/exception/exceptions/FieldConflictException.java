package com.bomdestino.sgm.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A custom exception to use when to happen a bad request error because of an entity field problem.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FieldConflictException extends RuntimeException {

    public FieldConflictException(String message) {
        super(message);
    }

}
