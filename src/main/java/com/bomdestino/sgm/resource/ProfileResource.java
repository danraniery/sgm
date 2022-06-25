package com.bomdestino.sgm.resource;

import com.bomdestino.sgm.config.security.auth.SGMRole;
import com.bomdestino.sgm.domain.Profile;
import com.bomdestino.sgm.dto.AbstractListDTO;
import com.bomdestino.sgm.dto.ProfileListResponseDTO;
import com.bomdestino.sgm.dto.ProfileRequestDTO;
import com.bomdestino.sgm.dto.ProfileResponseDTO;
import com.bomdestino.sgm.service.ProfileService;
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
import java.util.Set;

import static com.bomdestino.sgm.util.Authorities.*;
import static com.bomdestino.sgm.util.EndpointConstants.*;

/**
 * REST controller to provide access for the {@link Profile} resources.
 */
@AllArgsConstructor
@RestController
@RequestMapping(PROFILE_URL)
public class ProfileResource {

    private final ProfileService profileService;

    /**
     * {@code GET  /profiles} : get all profiles from the system.
     *
     * @param name     it's the value to filter the profiles by the *name*.
     * @param pageable it's the page configuration.
     * @return a page of {@link ProfileListResponseDTO} with the profiles from the database.
     */
    @GetMapping
    @Secured({AUDITOR, PROFILE_MANAGEMENT})
    public ResponseEntity<Page<ProfileListResponseDTO>> getAllProfiles(@RequestParam(required = false, defaultValue = "") String name,
                                                                       @SortDefault(sort = "name", direction = Sort.Direction.ASC)
                                                                       @PageableDefault(size = 20) Pageable pageable) {
        Page<ProfileListResponseDTO> page = profileService.getAllProfiles(name, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * {@code GET  /profiles/activated} : get all activated profiles from the system.
     *
     * @param pageable it's the page configuration.
     * @return a page of {@link AbstractListDTO} with the profiles from the database.
     */
    @GetMapping(PARAMS_ACTIVATED)
    @Secured({AUDITOR, PROFILE_MANAGEMENT, USER_MANAGEMENT})
    public ResponseEntity<Page<AbstractListDTO>> getAllActivatedProfiles(@SortDefault(sort = "name", direction = Sort.Direction.ASC)
                                                                         @PageableDefault(size = 20) Pageable pageable) {
        Page<AbstractListDTO> page = profileService.getAllActivatedProfiles(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * {@code GET /profiles/:id} : get the profile from the system by id.
     *
     * @param id it's the id of the profile to be found.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the {@link ProfileResponseDTO}.
     */
    @GetMapping(PARAMS_ID)
    @Secured({AUDITOR, PROFILE_MANAGEMENT})
    public ResponseEntity<ProfileResponseDTO> getProfilesById(@PathVariable Long id) {
        return ResponseEntity.ok().body(new ProfileResponseDTO(profileService.getProfileById(id)));
    }

    /**
     * {@code POST  /profiles}  : create a new Profile.
     *
     * @param profileDTO it's the dto containing all data to be saved.
     * @return the ResponseEntity with status 201 (created).
     */
    @PostMapping
    @Secured(PROFILE_MANAGEMENT)
    public ResponseEntity<URI> createProfile(@Valid @RequestBody ProfileRequestDTO profileDTO) {
        Profile profile = profileService.createProfile(profileDTO);
        return ResponseEntity.created(ResponseUtils.toURI(profile.getId())).build();
    }

    /**
     * {@code PUT /profiles/:id} : update an existing Profile.
     *
     * @param profileDTO it's the dto containing profile data to be updated.
     * @return the ResponseEntity with status 200 (OK).
     */
    @PutMapping(PARAMS_ID)
    @Secured(PROFILE_MANAGEMENT)
    public ResponseEntity<URI> updateProfile(@PathVariable Long id, @Valid @RequestBody ProfileRequestDTO profileDTO) {
        Profile profile = profileService.updateProfile(id, profileDTO);
        return ResponseEntity.ok().location(ResponseUtils.toURI(profile.getId())).build();
    }

    /**
     * {@code DELETE /profiles/:id/logic} : delete logically the Profile.
     *
     * @param id it's the id of the profile to be disabled.
     * @return the ResponseEntity with status 200 (OK).
     */
    @DeleteMapping(PARAMS_DISABLE)
    @Secured(PROFILE_MANAGEMENT)
    public ResponseEntity<URI> disableProfile(@PathVariable Long id) {
        Profile profile = profileService.logicalExclusion(id);
        return ResponseEntity.ok().location(ResponseUtils.toURI(profile.getId())).build();
    }

    /**
     * {@code GET /roles} : get all roles from the system.
     *
     * @return a set of {@link SGMRole} with the roles from the system.
     */
    @GetMapping(ROLES)
    @Secured({AUDITOR, PROFILE_MANAGEMENT})
    public ResponseEntity<Set<SGMRole>> getAllRoles() {
        return ResponseEntity.ok().body(SGMRole.getValues());
    }

}
