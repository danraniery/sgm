package com.bomdestino.sgm.config.security.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This provides RBAC (Role-based access control) support for the system.
 */
@Getter
@AllArgsConstructor
public enum SGMRole {

    AUDITOR,
    PROFILE_MANAGEMENT,
    USER_MANAGEMENT;

    public SimpleGrantedAuthority getGrantedAuthority() {
        return new SimpleGrantedAuthority("ROLE_" + this.name());
    }

    public static Set<SGMRole> getValues() {
        return Arrays.stream(SGMRole.values()).sorted(Comparator.comparing(Enum::name))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
