package com.really.good.sir.filter;

import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.entity.UserSessionEntity;
import com.really.good.sir.security.AppSecurityContext;
import jakarta.annotation.Priority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.servlet.http.HttpServletRequest;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SessionAuthFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = LogManager.getLogger(SessionAuthFilter.class);
    private final UserSessionDAO sessionDAO = new UserSessionDAO();

    @Context
    private HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath(true);
        LOGGER.info("auth");
        // skip login endpoint
        if (path.startsWith("api/auth/login")) return;
        Integer sessionId = null;

        javax.ws.rs.core.Cookie cookie = requestContext.getCookies().get("session_id");
        if (cookie != null) {
            try {
                sessionId = Integer.parseInt(cookie.getValue());
            } catch (NumberFormatException ignored) {
            }
        }

        if (sessionId == null) {
            abort(requestContext, "Missing session id");
            return;
        }

        UserSessionEntity session = sessionDAO.getSessionById(sessionId);
        if (session == null) {
            abort(requestContext, "Invalid or expired session");
            return;
        }

        boolean isSecure = servletRequest != null && servletRequest.isSecure();
        LOGGER.info(">>>>>>>>>>>>>>> session " + session);
        LOGGER.info(">>>>>>>>>>>>>>> isSecure " + isSecure);
        requestContext.setSecurityContext(new AppSecurityContext(session, isSecure));
    }

    private void abort(ContainerRequestContext ctx, String message) {
        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\":\"" + message + "\"}")
                .type("application/json")
                .build());
    }
}

