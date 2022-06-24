package com.bomdestino.sgm.config.security.auth;

import com.bomdestino.sgm.domain.User;
import com.bomdestino.sgm.util.Translator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.bomdestino.sgm.util.TranslateConstants.NOT_FOUND_MESSAGE;
import static com.bomdestino.sgm.util.TranslateConstants.USER_ENTITY;


@Service
@RequiredArgsConstructor
public class SGMUserDetailsService implements UserDetailsService {

    private final Translator translator;
    private final IAccountDao iAccountDao;

    /**
     * Returns a {@link UserDetails} based on a {@link User} from the database.
     *
     * @param username it's the user's username.
     * @return the user that has been found in the database.
     * @throws UsernameNotFoundException if the user isn't exists.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new SGMUserDetails(iAccountDao.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(translator.translate(NOT_FOUND_MESSAGE),
                        translator.translate(USER_ENTITY)))));
    }

}
