package com.bomdestino.sgm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;

/**
 * Application translate keys constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TranslateConstants {

    /**
     * General keys
     */
    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String NOT_FOUND_MESSAGE = "error.globalNotFound";
    public static final String ACCESS_DENIED_MESSAGE = "error.accessDenied";
    public static final String EDIT_SUPER_ENTITY_MESSAGE = "error.editSuperEntity";
    public static final String BAD_CREDENTIALS_MESSAGE = "error.badCredentials";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String USER_BLOCKED = "error.loginUserIsBlocked";
    public static final String USER_NOT_ACTIVATED = "error.loginUserNotActivated";
    public static final String LOGON_ATTEMPTS_EXCEEDED_MESSAGE = "error.password.logonAttemptsExceeded";
    public static final String ACCOUNT_SESSION_EXPIRED = "error.session.expired";
    public static final String INVALID_REFRESH_TOKEN = "error.session.invalidToken";

    public static final URI DEFAULT_TYPE = URI.create("/problem");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create("/constraint-violation");

    /**
     * Fields
     */
    public static final String DESCRIPTION_KEY = ".description";

    /**
     * Profiles
     */
    public static final String SUPER_ADMIN_PROFILE = "Super Admin";

    /**
     * User keys
     */
    public static final String USER_ENTITY = "user";

    /**
     * Profile keys
     */
    public static final String PROFILE_ENTITY = "profile";

}
