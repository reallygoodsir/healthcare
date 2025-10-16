package com.really.good.sir.security;

import com.really.good.sir.entity.UserSessionEntity;
import com.really.good.sir.filter.SessionAuthFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class AppSecurityContext implements SecurityContext {
    private static final Logger LOGGER = LogManager.getLogger(AppSecurityContext.class);

    private final UserSessionEntity session;
    private final boolean isSecure;

    public AppSecurityContext(UserSessionEntity session, boolean isSecure) {
        this.session = session;
        this.isSecure = isSecure;
    }

    @Override
    public Principal getUserPrincipal() {
        LOGGER.info("getUserPrincipal");
        if (session == null) {
            LOGGER.info("getUserPrincipal null");
            return null;
        }
        return () -> String.valueOf(session.getCredentialId());
    }

    @Override
    public boolean isUserInRole(String role) {
        LOGGER.info("isUserInRole");
        if (session == null) return false;
        return role.equalsIgnoreCase(session.getRole());
    }

    @Override
    public boolean isSecure() {
        LOGGER.info("isSecure");
        return isSecure;
    }

    @Override
    public String getAuthenticationScheme() {
        LOGGER.info("getAuthenticationScheme");
        return "SESSION-ID";
    }

    public UserSessionEntity getSession() {
        LOGGER.info("getSession");
        return session;
    }
}
