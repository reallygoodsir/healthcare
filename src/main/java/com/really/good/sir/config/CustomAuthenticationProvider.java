package com.really.good.sir.config;

import com.really.good.sir.dto.UserSessionDTO;
import com.really.good.sir.service.UserSessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static final Logger LOGGER = LogManager.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    private UserSessionService userSessionService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        LOGGER.info("======================================================provider1");
        Object principal = authentication.getPrincipal();
        LOGGER.info("======================================================" + principal);

        UserSessionDTO session = userSessionService.getSessionById((Integer) principal);
        LOGGER.info("======================================================" + session);

        if (session != null) {
            return new UsernamePasswordAuthenticationToken(
                    session,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + session.getRole()))
            );
        }
        LOGGER.info("------provider before null" );

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
