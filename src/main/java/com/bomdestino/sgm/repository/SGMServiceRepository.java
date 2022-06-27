package com.bomdestino.sgm.repository;

import com.bomdestino.sgm.domain.Area;
import com.bomdestino.sgm.domain.SGMService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link SGMService} entity.
 */
@Repository
public interface SGMServiceRepository extends JpaRepository<SGMService, Long> {

    SGMService findByNameIgnoreCase(String name);

    Optional<SGMService> findSGMServiceByNameIgnoreCase(String name);

    Page<SGMService> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<SGMService> findAllByAreasWithinAndActivatedIsTrue(Area area, Pageable pageable);

}
