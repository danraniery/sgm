package com.bomdestino.sgm.service;

import com.bomdestino.sgm.domain.Area;
import com.bomdestino.sgm.domain.Profile;
import com.bomdestino.sgm.domain.SGMService;
import com.bomdestino.sgm.dto.ServiceCardResponseDTO;
import com.bomdestino.sgm.dto.ServiceListResponseDTO;
import com.bomdestino.sgm.dto.ServiceRequestDTO;
import com.bomdestino.sgm.exception.exceptions.FieldConflictException;
import com.bomdestino.sgm.exception.exceptions.NotFoundException;
import com.bomdestino.sgm.repository.SGMServiceRepository;
import com.bomdestino.sgm.util.Translator;
import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static com.bomdestino.sgm.util.TranslateConstants.*;

/**
 * Service class for {@link SGMService} entity management.
 */
@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class SGMServicesService {

    private final Translator translator;
    private final AreaService areaService;
    private final SGMServiceRepository sgmServiceRepository;

    /**
     * Get a {@link SGMService} by id.
     *
     * @param id it's the id of the service to be found.
     * @return the {@link SGMService} from the database.
     * @throws NotFoundException if the service doesn't exist in the database.
     */
    public SGMService getServiceById(Long id) {
        return sgmServiceRepository.findById(id).orElseThrow(() -> new NotFoundException(SGMSERVICE_ENTITY));
    }

    /**
     * Get all services from the database.
     * <p>
     * It can be ordered by any attributes of the entity *SGMService*.
     * It can be filtered by the *name* attribute.
     *
     * @param search   it's the value of the filter to search the services.
     * @param pageable it's the page configuration.
     * @return a page with a list of {@link ServiceListResponseDTO} and some pagination information.
     */
    public Page<ServiceListResponseDTO> getAllServices(String search, Pageable pageable) {
        Page<SGMService> sgmServicePage;

        if (!Strings.isNullOrEmpty(search)) {
            sgmServicePage = sgmServiceRepository.findAllByNameContainingIgnoreCase(search, pageable);
        } else {
            sgmServicePage = sgmServiceRepository.findAll(pageable);
        }

        log.debug("Returning services sorted by '{}' | searched by '{}': {}", pageable.getSort(), search, sgmServicePage.getContent());

        return sgmServicePage.map(ServiceListResponseDTO::new);
    }

    /**
     * Get all activated services from the database.
     *
     * @param areaId   it's the id of the area that the services belongs.
     * @param pageable it's the page configuration.
     * @return a page with a list of {@link ServiceCardResponseDTO} and some pagination information.
     */
    public Page<ServiceCardResponseDTO> getAllActivatedServices(Long areaId, Pageable pageable) {
        Area area = areaService.getAreaById(areaId);
        return sgmServiceRepository.findAllByAreasWithinAndActivatedIsTrue(area, pageable)
                .map(ServiceCardResponseDTO::new);
    }

    /**
     * Create a new {@link SGMService}.
     *
     * @param dto it's a dto containing all data to be saved.
     * @return the {@link SGMService} that has been created.
     */
    public SGMService createSGMService(ServiceRequestDTO dto) {
        SGMService service = new SGMService();
        BeanUtils.copyProperties(dto, service);
        verifyServiceNameConflict(dto.getName(), dto.getId());
        return sgmServiceRepository.save(service);
    }

    /**
     * Update an existing {@link SGMService}.
     *
     * @param id  it's the id of the service to be updated.
     * @param dto it's a dto containing all service data to be updated.
     * @return the {@link Profile} that has been updated.
     */
    public SGMService updatePSGMService(Long id, ServiceRequestDTO dto) {
        SGMService service = getServiceById(id);
        verifyServiceNameConflict(dto.getName(), dto.getId());
        BeanUtils.copyProperties(dto, service);
        return sgmServiceRepository.save(service);
    }

    /**
     * Update the {@link SGMService} *activated* status.
     *
     * @param id it's the id of the service to be updated.
     * @return the {@link SGMService} that has been updated.
     */
    public SGMService logicalExclusion(Long id) {
        SGMService service = getServiceById(id);
        service.setActivated(!service.isActivated());
        return sgmServiceRepository.save(service);
    }

    /**
     * Verify if the field has been in use by other service.
     *
     * @param name is the name of the service to be checked for duplicates.
     * @throws FieldConflictException if there is another service using the same value.
     */
    private void verifyServiceNameConflict(String name, Long newServiceId) {
        Optional<SGMService> service = sgmServiceRepository.findSGMServiceByNameIgnoreCase(name);
        if (Objects.isNull(newServiceId) && service.isPresent() || Objects.nonNull(newServiceId) && service.isPresent()
                && !newServiceId.equals(service.get().getId())) {
            throw new FieldConflictException(String.format(translator.translate(CONFLICT_FIELD_MALE),
                    translator.translate(SGMSERVICE_ENTITY), translator.translate(FIELD_NAME)));
        }
    }

}
