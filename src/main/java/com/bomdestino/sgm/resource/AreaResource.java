package com.bomdestino.sgm.resource;

import com.bomdestino.sgm.domain.Area;
import com.bomdestino.sgm.dto.AbstractListDTO;
import com.bomdestino.sgm.service.AreaService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bomdestino.sgm.util.Authorities.AUDITOR;
import static com.bomdestino.sgm.util.Authorities.SERVICE_MANAGEMENT;
import static com.bomdestino.sgm.util.EndpointConstants.AREA_URL;
import static com.bomdestino.sgm.util.EndpointConstants.PARAMS_ACTIVATED;

/**
 * REST controller to provide access for the {@link Area} resources.
 */
@AllArgsConstructor
@RestController
@RequestMapping(AREA_URL)
public class AreaResource {

    private final AreaService areaService;

    /**
     * {@code GET  /areas/activated} : get all activated areas from the system.
     *
     * @param pageable it's the page configuration.
     * @return a page of {@link AbstractListDTO} with the areas from the database.
     */
    @GetMapping(PARAMS_ACTIVATED)
    @Secured({AUDITOR, SERVICE_MANAGEMENT})
    public ResponseEntity<Page<AbstractListDTO>> getAllActivatedAreas(@SortDefault(sort = "name", direction = Sort.Direction.ASC)
                                                                      @PageableDefault(size = 20) Pageable pageable) {
        Page<AbstractListDTO> page = areaService.getAllActivatedAreas(pageable);
        return ResponseEntity.ok(page);
    }

}
