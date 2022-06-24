package com.bomdestino.sgm.util;

import lombok.AllArgsConstructor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static com.bomdestino.sgm.util.Constants.SYSTEM_ADMIN_LANGUAGE;

/**
 * Service class for translating messages from keys.
 */
@Component
@AllArgsConstructor
public class Translator {

    private final ResourceBundleMessageSource messageSource;

    /**
     * Translate a message code to a text.
     *
     * @param messageCode it's the key to be translated.
     * @return a translated message if the code exist.
     */
    public String translate(String messageCode) {
        return messageSource.getMessage(messageCode, null, Locale.forLanguageTag(SYSTEM_ADMIN_LANGUAGE));
    }

}
