package com.bomdestino.sgm.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A custom exception to use when to happen a business rule error because of any problem.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }

}
