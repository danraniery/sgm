package com.bomdestino.sgm.repository;

import com.bomdestino.sgm.domain.Area;
import com.bomdestino.sgm.domain.Profile;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Area} entity.
 */
@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    Area findByNameIgnoreCase(String name);

    Page<Area> findAllByActivatedIsTrue(Pageable pageable);

}
