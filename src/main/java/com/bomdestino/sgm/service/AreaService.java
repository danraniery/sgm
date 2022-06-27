package com.bomdestino.sgm.service;

import com.bomdestino.sgm.domain.Area;
import com.bomdestino.sgm.dto.AbstractListDTO;
import com.bomdestino.sgm.exception.exceptions.NotFoundException;
import com.bomdestino.sgm.repository.AreaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bomdestino.sgm.util.TranslateConstants.AREA_ENTITY;

/**
 * Service class for {@link Area} entity management.
 */
@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    /**
     * Get a {@link Area} by id.
     *
     * @param id it's the id of the area to be found.
     * @return the {@link Area} from the database.
     * @throws NotFoundException if the area doesn't exist in the database.
     */
    public Area getAreaById(Long id) {
        return areaRepository.findById(id).orElseThrow(() -> new NotFoundException(AREA_ENTITY));
    }

    /**
     * Get all activated areas from the database.
     *
     * @param pageable it's the page configuration.
     * @return a page with a list of {@link AbstractListDTO} and some pagination information.
     */
    public Page<AbstractListDTO> getAllActivatedAreas(Pageable pageable) {
        return areaRepository.findAllByActivatedIsTrue(pageable)
                .map(area -> new AbstractListDTO(area.getId(), area.getName(), area.isActivated()));
    }

}
