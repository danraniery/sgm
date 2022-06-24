package com.bomdestino.sgm.config.security.auth;

import com.bomdestino.sgm.domain.User;

import java.util.Optional;

public interface IAccountDao {

    /**
     * Returns a User from the database.
     *
     * @param username it's the user's username.
     * @return the user that has been found in the database.
     */
    Optional<User> findUserByUsername(String username);

}
