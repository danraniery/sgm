package com.bomdestino.sgm.dto;

import com.bomdestino.sgm.domain.SGMService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A DTO representing a {@link SGMService} for details page.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponseDTO implements Serializable {

    private Long id;
    private String name;
    private String path;
    private boolean localPath;
    private boolean activated;
    private Set<AbstractListDTO> areas = new HashSet<>();

    public ServiceResponseDTO(@Valid SGMService service) {
        BeanUtils.copyProperties(service, this);

        service.getAreas().forEach(area -> {
            areas.add(new AbstractListDTO(area.getId(), area.getName(), area.isActivated()));
        });
    }

}
