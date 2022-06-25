package com.bomdestino.sgm.dto;

import com.bomdestino.sgm.config.security.auth.SGMRole;
import com.bomdestino.sgm.domain.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Set;

/**
 * A VO class representing a {@link Profile} for details page.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDTO implements Serializable {

    private Long id;
    private String name;
    private String description;
    private Set<SGMRole> roles;
    private boolean activated;
    private boolean onlyRead;

    public ProfileResponseDTO(Profile profile) {
        BeanUtils.copyProperties(profile, this);
    }

}
