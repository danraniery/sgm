package com.bomdestino.sgm.service;

import com.bomdestino.sgm.config.security.auth.IAccountDao;
import com.bomdestino.sgm.domain.User;
import com.bomdestino.sgm.exception.exceptions.BusinessRuleException;
import com.bomdestino.sgm.exception.exceptions.NotFoundException;
import com.bomdestino.sgm.exception.exceptions.UnauthorizedCredentialsException;
import com.bomdestino.sgm.exception.exceptions.UserNotActivatedException;
import com.bomdestino.sgm.repository.UserRepository;
import com.bomdestino.sgm.util.Translator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import static com.bomdestino.sgm.util.Constants.LOGON_ATTEMPT_CONTROL_INTERVAL_IN_SECONDS;
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

}
