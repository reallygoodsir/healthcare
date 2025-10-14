package com.really.good.sir.filter;

import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.entity.UserSessionEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;


@WebFilter("/*")
public class SecurityFilter implements Filter {
    private static final Logger LOGGER = LogManager.getLogger(SecurityFilter.class);
    private static final long SESSION_TIMEOUT_MINUTES = 30;

    private final UserSessionDAO userSessionDAO = new UserSessionDAO();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        LOGGER.info("Started security filter");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI();

        if (isIgnoredPath(path)) {
            chain.doFilter(req, res);
            return;
        }

        Integer sessionId = getSessionIdFromCookie(request);
        if (sessionId == null) {
            LOGGER.debug("No session cookie found");
            redirectToHome(response);
            return;
        }
        UserSessionEntity session = userSessionDAO.getSessionById(sessionId);
        if (session == null) {
            LOGGER.debug("Session not found for ID {}", sessionId);
            redirectToHome(response);
            return;
        }
        if (isExpired(session.getLoginDateTime())) {
            LOGGER.debug("Session {} expired", sessionId);
            redirectToHome(response);
            return;
        }
        String role = session.getRole();
        if (!isAuthorizedForPath(path, role)) {
            LOGGER.warn("Unauthorized access attempt by role [{}] to [{}]", role, path);
            redirectToHome(response);
            return;
        }
        chain.doFilter(req, res);
    }

    private boolean isIgnoredPath(String path) {
        // anything accessible without login

        return path.startsWith("/healthcare/api/") ||
                path.endsWith("/login.html")
                || path.endsWith("/home.html")
                || path.contains("/api/login")
                || path.contains("/css/")
                || path.contains("/js/")
                || path.contains("/images/");
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

    private boolean isExpired(Timestamp loginTime) {
        Instant loginInstant = loginTime.toInstant();
        Instant now = Instant.now();
        long minutes = Duration.between(loginInstant, now).toMinutes();
        return minutes > SESSION_TIMEOUT_MINUTES;
    }

    private boolean isAuthorizedForPath(String path, String role) {
        if (path.contains("/admin/")) {
            return "ADMIN".equals(role);
        } else if (path.contains("/agent/")) {
            return "CALL_CENTER_AGENT".equals(role);
        } else if (path.contains("/doctor/")) {
            return "DOCTOR".equals(role);
        } else if (path.contains("/patient/")) {
            return "PATIENT".equals(role);
        }
        return true; // public or base paths
    }

    private void redirectToHome(HttpServletResponse response) throws IOException {
        response.sendRedirect("/healthcare/home.html");
    }
}
