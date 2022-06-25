package com.bomdestino.sgm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.bomdestino.sgm.util.EndpointConstants.PARAMS_ID;

/**
 * Utility class to handle HTTP Responses.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseUtils {

    /**
     * Add the entity id on the URI and return it.
     *
     * @param entityId it's the id to be added on the URI.
     * @return the new URI with the entity id as param.
     */
    public static URI toURI(Long entityId) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path(PARAMS_ID).buildAndExpand(entityId).toUri();
    }

}
