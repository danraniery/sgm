package com.bomdestino.sgm.domain.enums;

/**
 * User type values.
 */
public enum UserType {

    NATURAL_PERSON("natural_person"),
    LEGAL_ENTITY("legal_entity");

    private final String type;

    UserType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
