package com.bomdestino.sgm.repository;

import com.bomdestino.sgm.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameIgnoreCase(String username);

    Optional<User> findByUsername(String username);

    Page<User> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

}
