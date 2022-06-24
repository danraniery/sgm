package com.bomdestino.sgm.repository;

import com.bomdestino.sgm.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Profile} entity.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Profile findByNameIgnoreCase(String name);

}
