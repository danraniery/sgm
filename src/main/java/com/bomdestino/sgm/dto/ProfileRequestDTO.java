package com.bomdestino.sgm.dto;

import com.bomdestino.sgm.config.security.auth.SGMRole;
import com.bomdestino.sgm.domain.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.bomdestino.sgm.util.TranslateConstants.*;

/**
 * A DTO class representing a {@link Profile}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProfileRequestDTO {

    private Long id;

    @NotBlank(message = PROFILE_FIELD_NAME_BLANK)
    @Size(min = 2, max = 100, message = PROFILE_FIELD_NAME_SIZE)
    private String name;

    @Size(max = 256, message = PROFILE_FIELD_DESCRIPTION_SIZE)
    private String description;

    private Set<SGMRole> roles;

    private boolean activated;

}
