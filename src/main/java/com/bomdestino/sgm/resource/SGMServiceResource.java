package com.bomdestino.sgm.resource;

import com.bomdestino.sgm.domain.SGMService;
import com.bomdestino.sgm.dto.*;
import com.bomdestino.sgm.service.SGMServicesService;
import com.bomdestino.sgm.util.ResponseUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static com.bomdestino.sgm.util.Authorities.*;
import static com.bomdestino.sgm.util.EndpointConstants.*;

/**
 * REST controller to provide access for the {@link SGMService} resources.
 */
@AllArgsConstructor
@RestController
@RequestMapping(SERVICE_URL)
public class SGMServiceResource {

    private final SGMServicesService sgmServicesService;

    /**
     * {@code GET  /services} : get all services from the system.
     *
     * @param name     it's the value to filter the services by the *name*.
     * @param pageable it's the page configuration.
     * @return a page of {@link ServiceListResponseDTO} with the services from the database.
     */
    @GetMapping
    @Secured({AUDITOR, SERVICE_MANAGEMENT})
    public ResponseEntity<Page<ServiceListResponseDTO>> getAllServices(@RequestParam(required = false, defaultValue = "") String name,
                                                                       @SortDefault(sort = "name", direction = Sort.Direction.ASC)
                                                                       @PageableDefault(size = 20) Pageable pageable) {
        Page<ServiceListResponseDTO> page = sgmServicesService.getAllServices(name, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * {@code GET  /profiles/activated/:id} : get all activated services from the system by area.
     *
     * @param id       it's the id of the area that the services belongs.
     * @param pageable it's the page configuration.
     * @return a page of {@link ServiceCardResponseDTO} with the profiles from the database.
     */
    @GetMapping(PARAMS_ACTIVATED + PARAMS_ID)
    @Secured({AUDITOR, VIEW_CITIZEN_SERVICES, VIEW_HEALTH_SERVICES})
    public ResponseEntity<Page<ServiceCardResponseDTO>> getAllActivatedServices(@PathVariable Long id,
                                                                                @SortDefault(sort = "name", direction = Sort.Direction.ASC)
                                                                                @PageableDefault(size = 20) Pageable pageable) {
        Page<ServiceCardResponseDTO> page = sgmServicesService.getAllActivatedServices(id, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * {@code GET /services/:id} : get the service from the system by id.
     *
     * @param id it's the id of the service to be found.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the {@link ProfileResponseDTO}.
     */
    @GetMapping(PARAMS_ID)
    @Secured({AUDITOR, SERVICE_MANAGEMENT})
    public ResponseEntity<ServiceResponseDTO> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok().body(new ServiceResponseDTO(sgmServicesService.getServiceById(id)));
    }

    /**
     * {@code POST  /services}  : create a new SGMService.
     *
     * @param serviceRequestDTO it's the dto containing all data to be saved.
     * @return the ResponseEntity with status 201 (created).
     */
    @PostMapping
    @Secured(SERVICE_MANAGEMENT)
    public ResponseEntity<URI> createService(@Valid @RequestBody ServiceRequestDTO serviceRequestDTO) {
        SGMService service = sgmServicesService.createSGMService(serviceRequestDTO);
        return ResponseEntity.created(ResponseUtils.toURI(service.getId())).build();
    }

    /**
     * {@code PUT /services/:id} : update an existing SGMService.
     *
     * @param serviceRequestDTO it's the dto containing service data to be updated.
     * @return the ResponseEntity with status 200 (OK).
     */
    @PutMapping(PARAMS_ID)
    @Secured(SERVICE_MANAGEMENT)
    public ResponseEntity<URI> updateService(@PathVariable Long id, @Valid @RequestBody ServiceRequestDTO serviceRequestDTO) {
        SGMService service = sgmServicesService.updatePSGMService(id, serviceRequestDTO);
        return ResponseEntity.ok().location(ResponseUtils.toURI(service.getId())).build();
    }

    /**
     * {@code DELETE /services/:id/logic} : delete logically the SGMService.
     *
     * @param id it's the id of the service to be disabled.
     * @return the ResponseEntity with status 200 (OK).
     */
    @DeleteMapping(PARAMS_DISABLE)
    @Secured(SERVICE_MANAGEMENT)
    public ResponseEntity<URI> disableService(@PathVariable Long id) {
        SGMService service = sgmServicesService.logicalExclusion(id);
        return ResponseEntity.ok().location(ResponseUtils.toURI(service.getId())).build();
    }

}
