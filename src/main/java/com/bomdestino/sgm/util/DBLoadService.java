package com.bomdestino.sgm.util;

import com.bomdestino.sgm.config.security.auth.SGMRole;
import com.bomdestino.sgm.domain.Profile;
import com.bomdestino.sgm.domain.User;
import com.bomdestino.sgm.domain.enums.UserType;
import com.bomdestino.sgm.repository.ProfileRepository;
import com.bomdestino.sgm.repository.UserRepository;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.bomdestino.sgm.util.Constants.SYSTEM_ADMIN_FIRST_NAME;
import static com.bomdestino.sgm.util.Constants.SYSTEM_ADMIN_USERNAME;
import static com.bomdestino.sgm.util.TranslateConstants.DESCRIPTION_KEY;
import static com.bomdestino.sgm.util.TranslateConstants.SUPER_ADMIN_PROFILE;

/**
 * A service to create the initial data of the solution.
 */
@Transactional
@RequiredArgsConstructor
@Service
public class DBLoadService {

    @Value("${system.admin.password}")
    private String systemAdminPassword;

    private final Translator translator;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;

    private List<Profile> profiles;
    private List<User> users;

    /**
     * Init all data of the solution.
     */
    public void initData() {
        initLists();
        createProfiles();
        createUsers();
    }

    /**
     * Start the entities lists.
     */
    public void initLists() {
        profiles = new ArrayList<>();
        users = new ArrayList<>();
    }

    /**
     * Get the initial profiles list.
     *
     * @return a list with all initial profiles.
     */
    public List<Profile> getProfilesList() {

        Profile superAdminProfile = Profile.builder()
                .name(SUPER_ADMIN_PROFILE)
                .description(translator.translate(SUPER_ADMIN_PROFILE.toLowerCase(Locale.ROOT).replace(" ", "") + DESCRIPTION_KEY))
                .roles(Sets.newHashSet(
                        SGMRole.AUDITOR,
                        SGMRole.PROFILE_MANAGEMENT,
                        SGMRole.USER_MANAGEMENT))
                .onlyRead(true)
                .activated(true)
                .build();

        profiles.add(superAdminProfile);

        return profiles;

    }

    /**
     * Get the initial users list.
     *
     * @return a list with all initial users.
     */
    public List<User> getUsersList() {

        Profile systemAdminProfile = profileRepository.findByNameIgnoreCase(SUPER_ADMIN_PROFILE);
        String password = passwordEncoder.encode(systemAdminPassword);

        User systemUser = User.builder()
                .username(SYSTEM_ADMIN_USERNAME)
                .name(SYSTEM_ADMIN_FIRST_NAME)
                .password(password)
                .profile(systemAdminProfile)
                .lastPasswordUpdate(Instant.now())
                .lastLogonAttemptDate(Instant.now())
                .logonAttemptCounts(0)
                .lastPasswordHashes(new ArrayList<>(Collections.singletonList(password)))
                .superUser(true)
                .blocked(false)
                .activated(true)
                .type(UserType.LEGAL_ENTITY)
                .build();

        users.add(systemUser);

        return users;

    }

    /**
     * Create all initial profiles.
     */
    public void createProfiles() {
        for (Profile profile : getProfilesList()) {
            if (profileRepository.findByNameIgnoreCase(profile.getName()) == null) {
                profileRepository.save(profile);
            }
        }
    }

    /**
     * Create all initial users.
     */
    public void createUsers() {
        for (User user : getUsersList()) {
            if (userRepository.findByUsernameIgnoreCase(user.getUsername()) == null) {
                userRepository.save(user);
            }
        }
    }

}
