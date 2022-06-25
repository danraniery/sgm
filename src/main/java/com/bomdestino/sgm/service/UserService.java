package com.bomdestino.sgm.service;

import com.bomdestino.sgm.config.security.auth.IAccountDao;
import com.bomdestino.sgm.domain.User;
import com.bomdestino.sgm.domain.enums.UserType;
import com.bomdestino.sgm.dto.PasswordChangeRequestDTO;
import com.bomdestino.sgm.dto.UserListResponseDTO;
import com.bomdestino.sgm.dto.UserRequestDTO;
import com.bomdestino.sgm.exception.exceptions.*;
import com.bomdestino.sgm.repository.UserRepository;
import com.bomdestino.sgm.util.SecurityUtils;
import com.bomdestino.sgm.util.Translator;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.bomdestino.sgm.util.Constants.LOGON_ATTEMPT_CONTROL_INTERVAL_IN_SECONDS;
import static com.bomdestino.sgm.util.Constants.USER_PASSWORD_MIN_LENGTH;
import static com.bomdestino.sgm.util.TranslateConstants.*;

/**
 * Service class for {@link User} entity management.
 */
@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements IAccountDao {

    @Value("${password-rules.regex.number}")
    private String numberRegex;

    @Value("${password-rules.regex.lowercase}")
    private String lowercaseRegex;

    @Value("${password-rules.regex.uppercase}")
    private String uppercaseRegex;

    @Value("${password-rules.regex.non-alphabetic}")
    private String nonAlphabeticRegex;

    @Value("${password-rules.logon-attempts}")
    private Integer maxLogonAttempts;

    @Value("${password-rules.history-limit}")
    private Integer passwordHistorySizeLimit;

    private final Translator translator;
    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get an Optional<User> by username.
     *
     * @param username it's the username of the user to be found.
     * @return the Optional<User> from the database.
     */
    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get a {@link User} by id.
     *
     * @param id it's the id of the user to be found.
     * @return the {@link User} from the database.
     * @throws NotFoundException if the user doesn't exist in the database.
     */
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(USER_ENTITY));
    }

    /**
     * Get a {@link User} by username.
     *
     * @param username it's the username of the user to be found.
     * @return the {@link User} from the database.
     * @throws UsernameNotFoundException if the user doesn't exist on the database.
     */
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return findUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format(translator.translate(NOT_FOUND_MESSAGE),
                        translator.translate(USER_ENTITY))));
    }

    /**
     * Get the current logged {@link User} from the Spring Security Context.
     *
     * @return the current logged user.
     * @throws UsernameNotFoundException if the user doesn't exist on the database.
     */
    public User getLoggedUser() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findByUsername)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(translator.translate(NOT_FOUND_MESSAGE),
                        translator.translate(USER_ENTITY))));
    }

    /**
     * Get all users from the database.
     * <p>
     * It can be ordered by any attributes of the entity *User*.
     * It can be filtered by the *name* attribute.
     *
     * @param search   it's the value of the filter to search the users.
     * @param pageable it's the page configuration.
     * @return a page with a list of {@link UserListResponseDTO} and some pagination information.
     */
    public Page<UserListResponseDTO> getAllUsers(String search, Pageable pageable) {
        Page<User> usersPage;

        if (!Strings.isNullOrEmpty(search)) {
            usersPage = userRepository.findAllByNameContainingIgnoreCaseAndSuperUserIsFalse(search, pageable);
        } else {
            usersPage = userRepository.findAllBySuperUserIsFalse(pageable);
        }

        log.debug("Returning users sorted by '{}' | searched by '{}': {}", pageable.getSort(), search, usersPage.getContent());

        return usersPage.map(UserListResponseDTO::new);
    }

    /**
     * Create a new {@link User}.
     *
     * @param userDto it's a dto containing all data to be saved.
     * @return the {@link User} that has been created.
     */
    public User createUser(UserRequestDTO userDto) {
        checkAndValidateUserParameters(null, userDto);
        User user = mapDTOToUser(null, userDto);
        user.setSuperUser(false);
        user.setBlocked(false);
        return userRepository.save(user);
    }

    /**
     * Update an existing {@link User}.
     *
     * @param id      it's the id of the user to be updated.
     * @param userDto it's a dto containing all user data to be updated.
     * @return the {@link User} that has been updated.
     */
    public User updateUser(Long id, UserRequestDTO userDto) {
        checkAndValidateUserParameters(id, userDto);
        User user = mapDTOToUser(id, userDto);
        return userRepository.save(user);
    }

    /**
     * Update the {@link User} *activated* status.
     * <p>
     * It will change the *blocked* status if the user is activated.
     *
     * @param id it's the id of the user to be updated.
     * @return the {@link User} that has been updated.
     */
    public User logicalExclusion(Long id) {
        User user = getUserById(id);
        validateSuperUser(user);

        if (user.isBlocked()) {
            user.setBlocked(false);
            resetBadLoginAttempts(user.getUsername());
            return userRepository.save(user);
        }

        user.setActivated(!user.isActivated());
        return userRepository.save(user);
    }

    /**
     * Restart the {@link User} login attempts counter.
     *
     * @param username it's the username of the user to be updated.
     */
    public void resetBadLoginAttempts(String username) {
        User user = getUserByUsername(username);

        if (!user.isSuperUser() && user.getLogonAttemptCounts() >= 1) {
            user.setLogonAttemptCounts(0);
            user.setLastLogonAttemptDate(Instant.now());
            userRepository.save(user);
        }

    }

    /**
     * Update the {@link User} login attempts counter.
     *
     * @param username it's the username of the user to be updated.
     * @return the user blocked status.
     */
    public Boolean registerLogonAttemptWithBadCredentials(String username) {
        User user = getUserByUsername(username);

        if (!user.isSuperUser()) {
            if (Objects.isNull(user.getLastLogonAttemptDate()) ||
                    user.getLastLogonAttemptDate().isBefore(Instant.now().minusSeconds(LOGON_ATTEMPT_CONTROL_INTERVAL_IN_SECONDS))) {
                user.setLogonAttemptCounts(1);
            } else {
                user.setLogonAttemptCounts(user.getLogonAttemptCounts() + 1);
            }
            user.setLastLogonAttemptDate(Instant.now());
            if (user.getLogonAttemptCounts() >= maxLogonAttempts) {
                setBlockStatusUser(user);
            }
            userRepository.save(user);
            return user.isBlocked();
        }

        return false;
    }

    /**
     * Verify if the user exists and if he/she is allowed to access the system.
     *
     * @param username its the username of the user to be verified.
     */
    public void verifyUserCredentials(String username) {
        User user;
        try {
            user = getUserByUsername(username);
        } catch (UsernameNotFoundException exception) {
            throw new UnauthorizedCredentialsException(translator.translate(BAD_CREDENTIALS_MESSAGE));
        }
        verifyActivatedUser(user);
        verifyBlockedUser(user);
    }

    /**
     * Verify if the user is activated.
     *
     * @param user it's the user to be verified.
     * @throws UserNotActivatedException if the user isn't active.
     */
    public void verifyActivatedUser(User user) {
        if (Boolean.FALSE.equals(user.isActivated())) {
            throw new UserNotActivatedException(translator.translate(USER_NOT_ACTIVATED));
        }
    }

    /**
     * Verify if the user is blocked.
     *
     * @param user it's the user username or e-mail to be verified.
     * @throws UnauthorizedCredentialsException if the user usernameOrEmail is invalid.
     * @throws BusinessRuleException            if the user is blocked.
     */
    public void verifyBlockedUser(User user) {
        if (user.isBlocked()) {
            if (user.getLastLogonAttemptDate().isBefore(Instant.now().minusSeconds(LOGON_ATTEMPT_CONTROL_INTERVAL_IN_SECONDS))) {
                setBlockStatusUser(user);
            } else {
                throw new BusinessRuleException(translator.translate(USER_BLOCKED));
            }
        }
    }

    /**
     * Update the {@link User} blocked status.
     *
     * @param user it's the user to be updated.
     */
    public void setBlockStatusUser(User user) {
        validateSuperUser(user);
        user.setBlocked(!user.isBlocked());
        userRepository.save(user);
    }

    /**
     * Validate the password params and update the password of the logged user.
     *
     * @param dto it's the dto containing the new logged user password.
     * @return the {@link User} that has been updated.
     */
    public User validateAndUpdatePassword(PasswordChangeRequestDTO dto) {
        User currentUser = getLoggedUser();
        checkPasswordNullOrEmpty(currentUser.getId(), dto.getPassword());
        validatePasswordPattern(dto.getPassword());
        checkPasswordUniqueness(currentUser.getId(), dto.getPassword());
        checkDifferentPassword(dto.getPassword(), dto.getConfirmPassword());
        currentUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        resetParametersOnPasswordUpdate(currentUser, dto.getPassword());
        return userRepository.save(currentUser);
    }

    /**
     * Validate if the user is the super administrative user.
     *
     * @param user it's the user to be validated.
     * @throws UnauthorizedCredentialsException with the message "Access denied! You cannot edit that user." if the user is the super administrative user.
     */
    private void validateSuperUser(User user) {
        if (user.isSuperUser()) {
            throw new UnauthorizedCredentialsException(String.format(translator.translate(EDIT_SUPER_ENTITY_MESSAGE),
                    translator.translate(USER_ENTITY)));
        }
    }

    /**
     * Verify if the password is null or empty on creating user.
     *
     * @param userId   it's the user id to be verified. If it's calling on creating user, that value must be null.
     * @param password it's the user password to be verified.
     * @throws BusinessRuleException if the user id is null and the password is null or empty.
     */
    private void checkPasswordNullOrEmpty(Long userId, String password) {
        if (Objects.isNull(userId) && Strings.isNullOrEmpty(password)) {
            throw new BusinessRuleException(String.format(translator.translate(REQUIRED_MESSAGE),
                    translator.translate(FIELD_PASSWORD)));
        }
    }

    /**
     * Validate if the password contains all the necessary characters.
     * <p>
     * The password must contain at least 03 of those 04 types: Number, lower case letter, upper case letter and especial character.
     *
     * @param password it's the password to be validated.
     * @throws BusinessRuleException if the password doesn't contain the necessary characters.
     */
    private void validatePasswordPattern(String password) {

        if (password.length() < USER_PASSWORD_MIN_LENGTH) {
            throw new BusinessRuleException(translator.translate(PASSWORD_IS_LESS_THAN_REQUIRED_MESSAGE));
        }

        ArrayList<Boolean> containsList = new ArrayList<>();
        containsList.add(Pattern.compile(numberRegex).matcher(password).find());
        containsList.add(Pattern.compile(lowercaseRegex).matcher(password).find());
        containsList.add(Pattern.compile(uppercaseRegex).matcher(password).find());
        containsList.add(Pattern.compile(nonAlphabeticRegex).matcher(password).find());

        if (containsList.stream().filter(result -> result).count() != 4) {
            throw new BusinessRuleException(translator.translate(PASSWORD_NOT_CONTAINS_MESSAGE));
        }

    }

    /**
     * Validate if the password is not the same of the latest 24 passwords that has been used for that user.
     *
     * @param userId   it's the user id to be verified. If it's calling on creating user, that value must be null.
     * @param password it's the password to be verified.
     * @throws BusinessRuleException if the new password is one of the latest 24 passwords that has been used.
     */
    private void checkPasswordUniqueness(Long userId, String password) {
        if (Objects.nonNull(userId)) {
            userRepository.findById(userId).ifPresent(user -> user.getLastPasswordHashes()
                    .forEach(oldPasswordHash -> {
                        if (passwordEncoder.matches(password, oldPasswordHash)) {
                            throw new BusinessRuleException(String.format(
                                    translator.translate(PASSWORD_EQUALS_OLD_PASSWORD_MESSAGE),
                                    translator.translate(USER_ENTITY)));
                        }
                    }));
        }
    }

    /**
     * Verify if the password and the confimation password fields are equals.
     *
     * @param password        it's the password to be verified.
     * @param confirmPassword it's the confimation password to be verified.
     * @throws FieldConflictException if the password and the confimation password are different.
     */
    private void checkDifferentPassword(String password, String confirmPassword) {
        if (!Strings.isNullOrEmpty(password) && !password.equalsIgnoreCase(confirmPassword)) {
            throw new FieldConflictException(translator.translate(PASSWORD_NOT_EQUAL_MESSAGE));
        }
    }

    /**
     * Restart the user login attempts and store the new password hash.
     *
     * @param user     it's the user to be updated.
     * @param password it's the new password to be stored.
     */
    private void resetParametersOnPasswordUpdate(User user, String password) {
        user.setLastPasswordUpdate(Instant.now());
        user.setLastPasswordHashes(updatePasswordHistory(user.getLastPasswordHashes(), password));
        user.setLogonAttemptCounts(0);
        user.setLastLogonAttemptDate(Instant.now());
    }

    /**
     * Update the user password hashes list.
     *
     * @param lastPasswordHashes it's the current user password hashes list.
     * @param newPassword        it's the new password to be stores as hash value.
     * @return the password hashes list that has been updated.
     */
    private List<String> updatePasswordHistory(List<String> lastPasswordHashes, String newPassword) {

        if (!Strings.isNullOrEmpty(newPassword)) {
            lastPasswordHashes.add(passwordEncoder.encode(newPassword));
            int passwordHistorySize = lastPasswordHashes.size();

            if (passwordHistorySize > passwordHistorySizeLimit) {
                lastPasswordHashes = lastPasswordHashes
                        .subList(passwordHistorySize - passwordHistorySizeLimit, passwordHistorySize);
            }
        }

        return lastPasswordHashes;
    }

    /**
     * Get a {@link User} entity from dto.
     *
     * @param userId  it's the id of the user on the database.
     * @param userDto it's a dto containing all data to be used on the User.
     * @return the {@link User} that has been created from the dto.
     */
    private User mapDTOToUser(Long userId, UserRequestDTO userDto) {
        User user = new User();

        if (Objects.nonNull(userId)) {
            user = getUserById(userId);
        }

        BeanUtils.copyProperties(userDto, user, "password");
        user.setId(userId);
        user.setUsername(userDto.getUsername().toLowerCase());

        if (!Strings.isNullOrEmpty(userDto.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            resetParametersOnPasswordUpdate(user, userDto.getPassword());
            user.setLastPasswordUpdate(null);
        }

        user.setProfile(profileService.getProfileById(userDto.getProfileId()));
        user.setType(getUserType(userDto.getType()));
        return user;
    }

    /**
     * Call all validators to the dto parameters.
     *
     * @param userId it's the user id to be verified if it's an update action.
     * @param dto    it's the dto containing all data to be verified.
     */
    private void checkAndValidateUserParameters(Long userId, UserRequestDTO dto) {
        verifyUserFieldConflict(dto.getUsername(), userId);
        checkPasswordNullOrEmpty(userId, dto.getPassword());
        if (!Strings.isNullOrEmpty(dto.getPassword())) {
            validatePasswordPattern(dto.getPassword());
            checkPasswordUniqueness(userId, dto.getPassword());
            checkDifferentPassword(dto.getPassword(), dto.getConfirmPassword());
        }
    }

    /**
     * Verify if the field has been in use by other user.
     *
     * @param username  is the username of the user to be checked for duplicates.
     * @param newUserId is the id of the user that is going to be created.
     * @throws FieldConflictException if there is another user using the same value.
     */
    private void verifyUserFieldConflict(String username, Long newUserId) {
        Optional<User> user = userRepository.findByUsername(username);
        if (Objects.isNull(newUserId) && user.isPresent() || Objects.nonNull(newUserId) && user.isPresent()
                && !newUserId.equals(user.get().getId())) {
            throw new FieldConflictException(String.format(translator.translate(CONFLICT_FIELD_MALE),
                    translator.translate(USER_ENTITY), translator.translate(FIELD_USERNAME)));
        }
    }

    /**
     * Get an {@link UserType} by its value.
     *
     * @param extension it's the value that the user type must have.
     * @return an {@link UserType} with the extension.
     * @throws BusinessRuleException if the extension isn't a valid asset type.
     */
    private UserType getUserType(String extension) {
        for (UserType userType : UserType.values()) {
            if (userType.getType().contains(extension)) {
                return userType;
            }
        }
        throw new BusinessRuleException(translator.translate(INVALID_USER_TYPE));
    }

}
