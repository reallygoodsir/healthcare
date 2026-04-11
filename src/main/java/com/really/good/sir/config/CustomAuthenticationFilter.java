package com.really.good.sir.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LogManager.getLogger(CustomAuthenticationFilter.class);

    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Use servletPath (correct for Spring Security matching)
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        String path = uri.substring(context.length());

        LOGGER.info("PATH: [" + path + "]");

        // Skip login endpoint (robust)
        if (path.startsWith("/api/authorization")
                || path.equals("/")
                || path.equals("/home.html")
                || path.equals("/login.html")
                || path.equals("/admin/admin.html")) {

            LOGGER.info("Publicly accessible endpoint skipped");
            filterChain.doFilter(request, response);
            return;
        }

        Integer sessionId = null;
        boolean invalidFormat = false;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("session_id".equals(cookie.getName())) {
                    try {
                        sessionId = Integer.parseInt(cookie.getValue());
                    } catch (NumberFormatException e) {
                        invalidFormat = true;
                    }
                }
            }
        }

        LOGGER.info("SESSION: " + sessionId + " | invalidFormat=" + invalidFormat);

        // Handle missing session
        if (sessionId == null && !invalidFormat) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"Session id is empty\"}");
            response.getWriter().flush();
            return;
        }

        // Handle invalid format
        if (invalidFormat) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"Not authorized. Session id has incorrect format\"}");
            response.getWriter().flush();
            return;
        }

        try {
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(sessionId, null);

            Authentication authResult = authenticationManager.authenticate(authRequest);

            SecurityContextHolder.getContext().setAuthentication(authResult);

        } catch (AuthenticationException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"Not authorized. Session id does not exist\"}");
            response.getWriter().flush();
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Integer getSessionIdFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("session_id".equals(cookie.getName())) {
                try {
                    return Integer.parseInt(cookie.getValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
}

