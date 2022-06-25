package com.bomdestino.sgm.dto;

import com.bomdestino.sgm.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A VO class representing a user, with its authorities.
 */
@Data
@NoArgsConstructor
public class AccountResponseDTO implements Serializable {

    private Long id;
    private String username;
    private String name;
    private String profile;
    private Set<String> authorities = new HashSet<>();
    private Boolean updatePassword;

    public AccountResponseDTO(User user) {
        BeanUtils.copyProperties(user, this);
        this.profile = user.getProfile().getName();
        SecurityContextHolder.getContext().getAuthentication().getAuthorities().forEach(authority ->
                this.authorities.add(authority.getAuthority()));

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime ninetyDaysAgo = now.plusDays(-60);

        this.updatePassword = (Objects.isNull(user.getLastPasswordUpdate()) ||
                user.getLastPasswordUpdate().isBefore(ninetyDaysAgo.toInstant())) && !user.isSuperUser();
    }

}
