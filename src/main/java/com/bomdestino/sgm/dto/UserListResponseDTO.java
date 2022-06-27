package com.bomdestino.sgm.dto;

import com.bomdestino.sgm.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * A VO class representing a {@link User} for list page, with its id, name and status.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListResponseDTO implements Serializable {

    private Long id;
    private String name;
    private String profile;
    private String type;
    private boolean activated;
    private boolean blocked;
    private boolean ruralProducer;

    public UserListResponseDTO(@Valid User user) {
        BeanUtils.copyProperties(user, this);
        this.profile = user.getProfile().getName();
        this.type = user.getType().getType();
    }

}
