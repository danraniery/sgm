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
    public static final String PARAMS_ACTIVATED = "/activated";
    public static final String PARAMS_ID = "/{id}";
    public static final String PARAMS_DISABLE = "/{id}/logic";
    public static final String ROLES = "/roles";

    /**
     * Authentication
     */
    public static final String AUTHENTICATE_URL = BASE_URL + "/authenticate";
    public static final String REFRESH_AUTHENTICATE_URL = "/refresh";
    public static final String ACCOUNT_URL = BASE_URL + "/account";
    public static final String UPDATE_PASSWORD_URL = BASE_URL + "/account/change-password";

    /**
     * User
     */
    public static final String USER_URL = BASE_URL + "/users";

    /**
     * Profile
     */
    public static final String PROFILE_URL = BASE_URL + "/profiles";

    /**
     * Area
     */
    public static final String AREA_URL = BASE_URL + "/areas";

    /**
     * Service
     */
    public static final String SERVICE_URL = BASE_URL + "/services";

}
