package com.bomdestino.sgm.dto;

import com.bomdestino.sgm.domain.SGMService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.List;

import static com.bomdestino.sgm.util.TranslateConstants.*;

/**
 * A DTO representing a {@link SGMService}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequestDTO {

    private Long id;

    @NotBlank(message = SGMSERVICE_FIELD_NAME_BLANK)
    @Size(min = 2, max = 100, message = SGMSERVICE_FIELD_NAME_SIZE)
    private String name;

    @NotBlank(message = SGMSERVICE_FIELD_PATH_BLANK)
    @Size(min = 2, max = 256, message = SGMSERVICE_FIELD_PATH_SIZE)
    private String path;

    @NotNull(message = USER_PROFILE_BLANK)
    private List<AbstractListDTO> areas;

    @NotNull(message = USER_RURAL_PRODUCER_BLANK)
    private boolean localPath;

    private boolean activated;

}
