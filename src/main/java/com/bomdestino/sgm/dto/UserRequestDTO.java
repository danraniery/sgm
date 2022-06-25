package com.bomdestino.sgm.dto;

import com.bomdestino.sgm.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.bomdestino.sgm.util.TranslateConstants.*;

/**
 * A DTO representing a {@link User}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    private Long id;

    @NotBlank(message = USER_USERNAME_BLANK)
    @Size(min = 2, max = 256, message = USER_USERNAME_SIZE)
    private String username;

    @NotBlank(message = USER_NAME_BLANK)
    @Size(min = 2, max = 100, message = USER_NAME_SIZE)
    private String name;

    private String password;

    private String confirmPassword;

    @NotNull(message = USER_PROFILE_BLANK)
    private Long profileId;

    @NotNull(message = USER_TYPE_BLANK)
    private String type;

    @NotNull(message = USER_RURAL_PRODUCER_BLANK)
    private boolean ruralProducer;

    private boolean activated;

}
