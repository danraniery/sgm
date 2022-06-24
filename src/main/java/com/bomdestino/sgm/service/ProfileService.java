package com.bomdestino.sgm.service;

import com.bomdestino.sgm.domain.Profile;
import com.bomdestino.sgm.exception.exceptions.NotFoundException;
import com.bomdestino.sgm.repository.ProfileRepository;
import com.bomdestino.sgm.util.Translator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bomdestino.sgm.util.TranslateConstants.PROFILE_ENTITY;

/**
 * Service class for {@link Profile} entity management.
 */
@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    /**
     * Get a {@link Profile} by name.
     *
     * @param name it's the name of the profile to be found.
     * @return the {@link Profile} from the database.
     */
    public Profile getProfileByName(String name) {
        return profileRepository.findByNameIgnoreCase(name);
    }

    /**
     * Get a {@link Profile} by id.
     *
     * @param id it's the id of the profile to be found.
     * @return the {@link Profile} from the database.
     * @throws NotFoundException if the profile doesn't exist in the database.
     */
    public Profile getProfileById(Long id) {
        return profileRepository.findById(id).orElseThrow(() -> new NotFoundException(PROFILE_ENTITY));
    }

}
