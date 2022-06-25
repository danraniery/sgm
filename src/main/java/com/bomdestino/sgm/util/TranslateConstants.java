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
    public static final String PERMISSION_DENIED_MESSAGE = "error.permissionDenied";
    public static final String EDIT_SUPER_ENTITY_MESSAGE = "error.editSuperEntity";
    public static final String BAD_CREDENTIALS_MESSAGE = "error.badCredentials";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String CONFLICT_FIELD_MALE = "error.conflictFieldMale";
    public static final String USER_BLOCKED = "error.loginUserIsBlocked";
    public static final String USER_NOT_ACTIVATED = "error.loginUserNotActivated";
    public static final String LOGON_ATTEMPTS_EXCEEDED_MESSAGE = "error.password.logonAttemptsExceeded";
    public static final String PASSWORD_NOT_CONTAINS_MESSAGE = "error.password.notContains";
    public static final String PASSWORD_IS_LESS_THAN_REQUIRED_MESSAGE = "error.password.less.then.required";
    public static final String PASSWORD_EQUALS_OLD_PASSWORD_MESSAGE = "error.password.newPassEqualsOldPassword";
    public static final String PASSWORD_NOT_EQUAL_MESSAGE = "error.password.notEqual";
    public static final String REQUIRED_MESSAGE = "error.requiredField";
    public static final String INVALID_USER_TYPE = "error.invalidUserType";
    public static final String ACCOUNT_SESSION_EXPIRED = "error.session.expired";
    public static final String INVALID_REFRESH_TOKEN = "error.session.invalidToken";

    public static final URI DEFAULT_TYPE = URI.create("/problem");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create("/constraint-violation");

    /**
     * Fields
     */
    public static final String DESCRIPTION_KEY = ".description";
    public static final String FIELD_NAME = "field.name";
    public static final String FIELD_USERNAME = "field.username";
    public static final String FIELD_PASSWORD = "field.password";

    /**
     * System Profiles
     */
    public static final String SUPER_ADMIN_PROFILE = "Super Admin";

    /**
     * User keys
     */
    public static final String USER_ENTITY = "user";
    public static final String USER_USERNAME_BLANK = "user.username.blank";
    public static final String USER_USERNAME_SIZE = "user.username.size";
    public static final String USER_NAME_BLANK = "user.name.blank";
    public static final String USER_NAME_SIZE = "user.name.size";
    public static final String USER_PASSWORD_BLANK = "user.password.blank";
    public static final String USER_PASSWORD_SIZE = "user.password.size";
    public static final String USER_CONFIRM_PASSWORD_BLANK = "user.confirm.password.blank";
    public static final String USER_CONFIRM_PASSWORD_SIZE = "user.confirm.password.size";
    public static final String USER_PROFILE_BLANK = "user.profile.blank";
    public static final String USER_TYPE_BLANK = "user.type.blank";
    public static final String USER_RURAL_PRODUCER_BLANK = "user.rural.blank";

    /**
     * Profile keys
     */
    public static final String PROFILE_ENTITY = "profile";
    public static final String PROFILE_FIELD_NAME_BLANK = "profile.name.blank";
    public static final String PROFILE_FIELD_NAME_SIZE = "profile.name.size";
    public static final String PROFILE_FIELD_DESCRIPTION_SIZE = "profile.description.size";

}
