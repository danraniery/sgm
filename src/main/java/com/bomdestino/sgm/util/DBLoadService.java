package com.bomdestino.sgm.util;

import com.bomdestino.sgm.config.security.auth.SGMRole;
import com.bomdestino.sgm.domain.Area;
import com.bomdestino.sgm.domain.Profile;
import com.bomdestino.sgm.domain.SGMService;
import com.bomdestino.sgm.domain.User;
import com.bomdestino.sgm.domain.enums.UserType;
import com.bomdestino.sgm.repository.AreaRepository;
import com.bomdestino.sgm.repository.ProfileRepository;
import com.bomdestino.sgm.repository.SGMServiceRepository;
import com.bomdestino.sgm.repository.UserRepository;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;

import static com.bomdestino.sgm.util.Constants.*;
import static com.bomdestino.sgm.util.TranslateConstants.*;

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
    private final AreaRepository areaRepository;
    private final PasswordEncoder passwordEncoder;
    private final SGMServiceRepository serviceRepository;
    private final ProfileRepository profileRepository;

    private List<Profile> profiles;
    private List<User> users;
    private List<Area> areas;
    private List<SGMService> SGMServices;

    /**
     * Init all data of the solution.
     */
    public void initData() {
        initLists();
        createProfiles();
        createUsers();
        createAreas();
        createServices();
    }

    /**
     * Start the entities lists.
     */
    public void initLists() {
        profiles = new ArrayList<>();
        users = new ArrayList<>();
        areas = new ArrayList<>();
        SGMServices = new ArrayList<>();
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
                        SGMRole.USER_MANAGEMENT,
                        SGMRole.SERVICE_MANAGEMENT,
                        SGMRole.VIEW_CITIZEN_SERVICES,
                        SGMRole.VIEW_HEALTH_SERVICES))
                .onlyRead(true)
                .activated(true)
                .build();

        profiles.add(superAdminProfile);

        Profile citizenProfile = Profile.builder()
                .name(CITIZEN_PROFILE)
                .description(translator.translate(CITIZEN_PROFILE.toLowerCase(Locale.ROOT).replace(" ", "") + DESCRIPTION_KEY))
                .roles(Sets.newHashSet(
                        SGMRole.VIEW_CITIZEN_SERVICES,
                        SGMRole.VIEW_HEALTH_SERVICES))
                .onlyRead(true)
                .activated(true)
                .build();

        profiles.add(citizenProfile);

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
     * Get the initial areas list.
     *
     * @return a list with all initial areas.
     */
    public List<Area> getAreasList() {

        Area citizenArea = Area.builder()
                .name(CITIZEN_AREA)
                .activated(true)
                .build();

        areas.add(citizenArea);

        Area healthArea = Area.builder()
                .name(HEALTH_AREA)
                .activated(true)
                .build();

        areas.add(healthArea);

        return areas;

    }

    /**
     * Get the initial services list.
     *
     * @return a list with all initial services.
     */
    public List<SGMService> getServicesList() {

        Set<Area> citizenArea = new HashSet<>();
        citizenArea.add(areaRepository.findByNameIgnoreCase(CITIZEN_AREA));

        Set<Area> healthArea = new HashSet<>();
        healthArea.add(areaRepository.findByNameIgnoreCase(HEALTH_AREA));

        Set<Area> bothAreas = new HashSet<>();
        bothAreas.add(areaRepository.findByNameIgnoreCase(CITIZEN_AREA));
        bothAreas.add(areaRepository.findByNameIgnoreCase(HEALTH_AREA));

        SGMService changePassword = SGMService.builder()
                .name(CHANGE_PASSWORD_SERVICE)
                .path(CHANGE_PASSWORD_PATH)
                .areas(citizenArea)
                .localPath(true)
                .activated(true)
                .build();

        SGMServices.add(changePassword);

        SGMService oficialDoc = SGMService.builder()
                .name(OFICIAL_DOC_SERVICE)
                .path(OFICIAL_DOC_PATH)
                .areas(citizenArea)
                .localPath(true)
                .activated(false)
                .build();

        SGMServices.add(oficialDoc);

        SGMService bills = SGMService.builder()
                .name(BILLS_SERVICE)
                .path(BILLS_PATH)
                .areas(citizenArea)
                .localPath(true)
                .activated(false)
                .build();

        SGMServices.add(bills);

        SGMService sac = SGMService.builder()
                .name(SAC_SERVICE)
                .path(SAC_PATH)
                .areas(citizenArea)
                .localPath(true)
                .activated(false)
                .build();

        SGMServices.add(sac);

        SGMService covid = SGMService.builder()
                .name(COVID_SERVICE)
                .path(COVID_PATH)
                .areas(healthArea)
                .localPath(false)
                .activated(true)
                .build();

        SGMServices.add(covid);

        SGMService news = SGMService.builder()
                .name(NEWS_SERVICE)
                .path(NEWS_PATH)
                .areas(bothAreas)
                .localPath(true)
                .activated(false)
                .build();

        SGMServices.add(news);

        return SGMServices;

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

    /**
     * Create all initial areas.
     */
    public void createAreas() {
        for (Area area : getAreasList()) {
            if (areaRepository.findByNameIgnoreCase(area.getName()) == null) {
                areaRepository.save(area);
            }
        }
    }

    /**
     * Create all initial services.
     */
    public void createServices() {
        for (SGMService SGMService : getServicesList()) {
            if (serviceRepository.findByNameIgnoreCase(SGMService.getName()) == null) {
                serviceRepository.save(SGMService);
            }
        }
    }


}
