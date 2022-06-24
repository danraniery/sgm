package com.bomdestino.sgm.config.security.auth;

import com.bomdestino.sgm.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
public class SGMUserDetails implements UserDetails {

    private final User user;
    private final Set<? extends GrantedAuthority> grantedAuthorities;

    public SGMUserDetails(User user) {
        this.user = user;
        grantedAuthorities = user
                .getProfile()
                .getRoles()
                .stream()
                .map(SGMRole::getGrantedAuthority)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
