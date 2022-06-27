package com.bomdestino.sgm.dto;

import com.bomdestino.sgm.domain.SGMService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * A VO class representing a {@link SGMService} for list page, with its id, name and status.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceListResponseDTO implements Serializable {

    private Long id;
    private String name;
    private String path;
    private boolean activated;

    public ServiceListResponseDTO(@Valid SGMService service) {
        BeanUtils.copyProperties(service, this);
    }

}
