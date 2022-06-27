package com.bomdestino.sgm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Application constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    /**
     * Global
     */
    public static final String SYSTEM_ACCOUNT = "system";

    /**
     * Authentication
     */
    public static final String AUTHORITIES_KEY = "auth";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_TOKEN = "Bearer ";
    public static final Long LOGON_ATTEMPT_CONTROL_INTERVAL_IN_SECONDS = 3600L;
    public static final String ERROR_KEY = "error";
    public static final String DETAIL_KEY = "detail";

    /**
     * Entities
     */
    public static final String PROFILE = "PROFILE";
    public static final String USER = "USER";
    public static final String SERVICE = "SGMSERVICE";
    public static final String AREA = "AREA";

    /**
     * User
     */
    public static final String SYSTEM_ADMIN_USERNAME = "system.admin";
    public static final String SYSTEM_ADMIN_FIRST_NAME = "Administrador";
    public static final String SYSTEM_ADMIN_LANGUAGE = "pt-br";
    public static final int USER_PASSWORD_MIN_LENGTH = 7;

    /**
     * Area
     */
    public static final String CITIZEN_AREA = "Cidadão";
    public static final String HEALTH_AREA = "Saúde";

    /**
     * Services
     */
    public static final String CHANGE_PASSWORD_SERVICE = "Atualizar Senha";
    public static final String CHANGE_PASSWORD_PATH = "/password";
    public static final String OFICIAL_DOC_SERVICE = "Diário Oficial";
    public static final String OFICIAL_DOC_PATH = "/oficial-doc";
    public static final String BILLS_SERVICE = "Impostos";
    public static final String BILLS_PATH = "/bills";
    public static final String SAC_SERVICE = "Contatos";
    public static final String SAC_PATH = "/sac";

    public static final String COVID_SERVICE = "Covid-19";
    public static final String COVID_PATH = "https://www.gov.br/anvisa/pt-br/assuntos/servicosdesaude/seguranca-do-paciente/covid-19";
    public static final String NEWS_SERVICE = "Notícicas";
    public static final String NEWS_PATH = "/news";

    /**
     * Fields
     */
    public static final String ID = "ID";
    public static final String ACTIVATED = "ACTIVATED";
    public static final String CREATED_BY = "CREATED_BY";
    public static final String CREATED_DATE = "CREATED_DATE";
    public static final String LAST_MODIFIED_BY = "LAST_MODIFIED_BY";
    public static final String LAST_MODIFIED_DATE = "LAST_MODIFIED_DATE";
    public static final String NAME = "NAME";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String PATH = "PATH";
    public static final String IS_LOCAL_PATH = "IS_LOCAL_PATH";
    public static final String ROLES = "ROLES";
    public static final String IS_ONLY_READ = "IS_ONLY_READ";
    public static final String IS_RURAL_PRODUCER = "IS_RURAL_PRODUCER";
    public static final String TYPE = "TYPE";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD_HASH = "PASSWORD_HASH";
    public static final String PROFILE_ID = "PROFILE_ID";
    public static final String LAST_PASSWORD_UPDATE = "LAST_PASSWORD_UPDATE";
    public static final String LAST_LOGON_ATTEMPT = "LAST_LOGON_ATTEMPT";
    public static final String LAST_LOGIN = "LAST_LOGIN";
    public static final String LOGON_ATTEMPT = "LOGON_ATTEMPT";
    public static final String LAST_PASSWORD_HASH = "LAST_PASSWORD_HASH";
    public static final String IS_SUPER_USER = "IS_SUPER_USER";
    public static final String IS_BLOCKED = "IS_BLOCKED";

}
