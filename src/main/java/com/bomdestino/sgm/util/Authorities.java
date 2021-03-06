package com.bomdestino.sgm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Authorities constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Authorities {

    /**
     * All system authorities
     */
    public static final String AUDITOR = "ROLE_AUDITOR";
    public static final String PROFILE_MANAGEMENT = "ROLE_PROFILE_MANAGEMENT";
    public static final String USER_MANAGEMENT = "ROLE_USER_MANAGEMENT";
    public static final String SERVICE_MANAGEMENT = "ROLE_SERVICE_MANAGEMENT";
    public static final String VIEW_CITIZEN_SERVICES = "ROLE_VIEW_CITIZEN_SERVICES";
    public static final String VIEW_HEALTH_SERVICES = "ROLE_VIEW_HEALTH_SERVICES";

}
