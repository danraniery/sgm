package com.bomdestino.sgm.service;

import com.bomdestino.sgm.domain.Profile;
import com.bomdestino.sgm.dto.AbstractListDTO;
import com.bomdestino.sgm.dto.ProfileListResponseDTO;
import com.bomdestino.sgm.dto.ProfileRequestDTO;
import com.bomdestino.sgm.exception.exceptions.BusinessRuleException;
import com.bomdestino.sgm.exception.exceptions.FieldConflictException;
import com.bomdestino.sgm.exception.exceptions.NotFoundException;
import com.bomdestino.sgm.repository.ProfileRepository;
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
 * Service class for {@link Profile} entity management.
 */
@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class ProfileService {

    private final Translator translator;
    private final ProfileRepository profileRepository;

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

    /**
     * Get all profiles from the database.
     * <p>
     * It can be ordered by any attributes of the entity *Profile*.
     * It can be filtered by the *name* attribute.
     *
     * @param search   it's the value of the filter to search the profiles.
     * @param pageable it's the page configuration.
     * @return a page with a list of {@link ProfileListResponseDTO} and some pagination information.
     */
    public Page<ProfileListResponseDTO> getAllProfiles(String search, Pageable pageable) {
        Page<Profile> profilePage;

        if (!Strings.isNullOrEmpty(search)) {
            profilePage = profileRepository.findAllByNameContainingIgnoreCase(search, pageable);
        } else {
            profilePage = profileRepository.findAll(pageable);
        }

        log.debug("Returning profiles sorted by '{}' | searched by '{}': {}", pageable.getSort(), search, profilePage.getContent());

        return profilePage.map(ProfileListResponseDTO::new);
    }

    /**
     * Get all activated profiles from the database.
     *
     * @param pageable it's the page configuration.
     * @return a page with a list of {@link AbstractListDTO} and some pagination information.
     */
    public Page<AbstractListDTO> getAllActivatedProfiles(Pageable pageable) {
        return profileRepository.findAllByActivatedIsTrue(pageable)
                .map(profile -> new AbstractListDTO(profile.getId(), profile.getName(), profile.isActivated()));
    }

    /**
     * Create a new {@link Profile}.
     *
     * @param profileDTO it's a dto containing all data to be saved.
     * @return the {@link Profile} that has been created.
     */
    public Profile createProfile(ProfileRequestDTO profileDTO) {
        Profile profile = new Profile();
        BeanUtils.copyProperties(profileDTO, profile);
        verifyProfileNameConflict(profileDTO.getName(), profileDTO.getId());
        return profileRepository.save(profile);
    }

    /**
     * Update an existing {@link Profile}.
     *
     * @param id         it's the id of the profile to be updated.
     * @param profileDTO it's a dto containing all profile data to be updated.
     * @return the {@link Profile} that has been updated.
     */
    public Profile updateProfile(Long id, ProfileRequestDTO profileDTO) {
        Profile profile = getProfileById(id);
        checkIfTheProfileIsEditable(profile);
        verifyProfileNameConflict(profileDTO.getName(), profileDTO.getId());
        BeanUtils.copyProperties(profileDTO, profile);
        return profileRepository.save(profile);
    }

    /**
     * Update the {@link Profile} *activated* status.
     *
     * @param id it's the id of the profile to be updated.
     * @return the {@link Profile} that has been updated.
     */
    public Profile logicalExclusion(Long id) {
        Profile profile = getProfileById(id);
        checkIfTheProfileIsEditable(profile);
        profile.setActivated(!profile.isActivated());
        return profileRepository.save(profile);
    }

    /**
     * Validate if the profile entered can be edited
     *
     * @param profile Profile to be checked
     * @throws BusinessRuleException if the profile cannot be edited.
     */
    private void checkIfTheProfileIsEditable(Profile profile) {
        if (profile.isOnlyRead()) {
            throw new BusinessRuleException(String.format(translator.translate(PERMISSION_DENIED_MESSAGE),
                    translator.translate(PROFILE_ENTITY)));
        }
    }

    /**
     * Verify if the field has been in use by other profile.
     *
     * @param name is the name of the profile to be checked for duplicates.
     * @throws FieldConflictException if there is another profile using the same value.
     */
    private void verifyProfileNameConflict(String name, Long newProfileId) {
        Optional<Profile> profile = profileRepository.findProfileByNameIgnoreCase(name);
        if (Objects.isNull(newProfileId) && profile.isPresent() || Objects.nonNull(newProfileId) && profile.isPresent()
                && !newProfileId.equals(profile.get().getId())) {
            throw new FieldConflictException(String.format(translator.translate(CONFLICT_FIELD_MALE),
                    translator.translate(PROFILE_ENTITY), translator.translate(FIELD_NAME)));
        }
    }

}
