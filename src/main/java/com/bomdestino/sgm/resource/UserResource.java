package com.bomdestino.sgm.resource;

import com.bomdestino.sgm.domain.User;
import com.bomdestino.sgm.dto.UserListResponseDTO;
import com.bomdestino.sgm.dto.UserRequestDTO;
import com.bomdestino.sgm.dto.UserResponseDTO;
import com.bomdestino.sgm.service.UserService;
import com.bomdestino.sgm.util.ResponseUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static com.bomdestino.sgm.util.Authorities.AUDITOR;
import static com.bomdestino.sgm.util.Authorities.USER_MANAGEMENT;
import static com.bomdestino.sgm.util.EndpointConstants.*;

/**
 * REST controller to provide access for the {@link User} resources.
 */
@AllArgsConstructor
@RestController
@RequestMapping(USER_URL)
public class UserResource {

    private final UserService userService;

    /**
     * {@code GET  /users} : get all users from the system.
     *
     * @param search   it's the value to search the users by the *name*.
     * @param pageable it's the page configuration.
     * @return a page of {@link UserListResponseDTO} with the users from the database.
     */
    @GetMapping
    @Secured({AUDITOR, USER_MANAGEMENT})
    public ResponseEntity<Page<UserListResponseDTO>> getAllUsers(@RequestParam(required = false, defaultValue = "") String search,
                                                                 @SortDefault(sort = "name", direction = Sort.Direction.ASC)
                                                                 @PageableDefault(size = 20) Pageable pageable) {
        Page<UserListResponseDTO> page = userService.getAllUsers(search, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * {@code GET /users/:id} : get the user from the system by id.
     *
     * @param id it's the id of the user to be found.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the {@link UserResponseDTO}.
     */
    @Transactional
    @GetMapping(PARAMS_ID)
    @Secured({AUDITOR, USER_MANAGEMENT})
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok().body(new UserResponseDTO(userService.getUserById(id)));
    }

    /**
     * {@code POST  /users}  : create a new User.
     *
     * @param userDTO it's the dto containing all data to be saved.
     * @return the ResponseEntity with status 201 (created).
     */
    @PostMapping
    @Secured(USER_MANAGEMENT)
    public ResponseEntity<URI> createUser(@Valid @RequestBody UserRequestDTO userDTO) {
        User user = userService.createUser(userDTO);
        return ResponseEntity.created(ResponseUtils.toURI(user.getId())).build();
    }

    /**
     * {@code PUT /users} : update an existing User.
     *
     * @param id      it's the id of the user to be updated.
     * @param userDTO it's the dto containing user data to be updated.
     * @return the ResponseEntity with status 200 (OK).
     */
    @PutMapping(PARAMS_ID)
    @Secured(USER_MANAGEMENT)
    public ResponseEntity<URI> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO userDTO) {
        User user = userService.updateUser(id, userDTO);
        return ResponseEntity.ok().location(ResponseUtils.toURI(user.getId())).build();
    }

    /**
     * {@code DELETE /users/:id/logic} : delete logically the User.
     *
     * @param id it's the id of the user to be disabled.
     * @return the ResponseEntity with status 200 (OK).
     */
    @DeleteMapping(PARAMS_DISABLE)
    @Secured(USER_MANAGEMENT)
    public ResponseEntity<URI> disableUser(@PathVariable Long id) {
        User user = userService.logicalExclusion(id);
        return ResponseEntity.ok().location(ResponseUtils.toURI(user.getId())).build();
    }

}
