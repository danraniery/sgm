package com.bomdestino.sgm.exception;

import lombok.Data;

import java.io.Serializable;

/**
 * A custom object to map problems.
 */
@Data
public class FieldErrorVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String objectName;

    private final String field;

    private final String message;

}
