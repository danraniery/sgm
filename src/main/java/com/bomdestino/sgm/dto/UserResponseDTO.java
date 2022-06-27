package com.bomdestino.sgm.dto;

import com.bomdestino.sgm.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * A VO class representing a {@link User} for details page.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO implements Serializable {

    private Long id;
    private String name;
    private String username;
    private String type;
    private AbstractListDTO profile;
    private boolean activated;
    private boolean ruralProducer;

    public UserResponseDTO(@Valid User user) {
        BeanUtils.copyProperties(user, this);
        this.profile = new AbstractListDTO(user.getProfile().getId(), user.getProfile().getName(), user.getProfile().isActivated());
        this.type = user.getType().getType();
    }

}
