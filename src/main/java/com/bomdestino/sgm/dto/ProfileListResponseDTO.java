package com.bomdestino.sgm.dto;

import com.bomdestino.sgm.domain.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * A VO class representing a {@link Profile} for list page, with its id, name, description, activated and superProfile.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileListResponseDTO implements Serializable {

    private Long id;
    private String name;
    private String description;
    private boolean activated;
    private boolean superProfile;
    private boolean onlyRead;

    public ProfileListResponseDTO(Profile profile) {
        BeanUtils.copyProperties(profile, this);
    }

}
