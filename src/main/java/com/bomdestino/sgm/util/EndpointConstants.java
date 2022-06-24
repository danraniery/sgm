package com.bomdestino.sgm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Application endpoint constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EndpointConstants {

    /**
     * General
     */
    private static final String BASE_URL = "/api";

    /**
     * Authentication
     */
    public static final String AUTHENTICATE_URL = BASE_URL + "/authenticate";
    public static final String REFRESH_AUTHENTICATE_URL = "/refresh";

}
