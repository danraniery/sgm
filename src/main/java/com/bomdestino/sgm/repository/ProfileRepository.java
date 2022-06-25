package com.bomdestino.sgm.repository;

import com.bomdestino.sgm.domain.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Profile} entity.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Profile findByNameIgnoreCase(String name);

    Optional<Profile> findProfileByNameIgnoreCase(String name);

    Page<Profile> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Profile> findAllByActivatedIsTrue(Pageable pageable);

}
