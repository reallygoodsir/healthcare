package com.really.good.sir.resources;

import com.really.good.sir.converter.UserSessionConverter;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.UserSessionDTO;
import com.really.good.sir.entity.UserSessionEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/authorization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorizationResource {
    private static final Logger LOGGER = LogManager.getLogger(AuthorizationResource.class);
    private final UserSessionDAO userSessionDAO = new UserSessionDAO();
    private final UserSessionConverter converter = new UserSessionConverter();

    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class SessionCheckRequest {
        public int sessionId;
    }

    @POST
    public Response authorize(LoginRequest request) {
        if (request == null || request.email == null || request.password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Email and password are required\"}")
                    .build();
        }

        UserSessionEntity sessionEntity = userSessionDAO.authorize(request.email, request.password);
        if (sessionEntity == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Invalid credentials\"}")
                    .build();
        }

        // Convert entity to DTO
        UserSessionDTO sessionDTO = converter.convert(sessionEntity);

        // Create session cookie
        NewCookie cookie = new NewCookie("session_id",
                String.valueOf(sessionDTO.getId()),
                "/", null,
                "User session ID",
                30 * 60, false);

        LOGGER.info("User logged in with credential_id {} and role {}", sessionDTO.getCredentialId(), sessionDTO.getRole());

        // Return the DTO itself, not a Map
        return Response.ok(sessionDTO).cookie(cookie).build();
    }

    @POST
    @Path("/check-session")
    public Response checkSession(SessionCheckRequest request) {
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Session ID is required\"}")
                    .build();
        }

        UserSessionEntity sessionEntity = userSessionDAO.getSessionById(request.sessionId);
        if (sessionEntity == null) {
            return Response.ok(new UserSessionDTO()).build(); // empty DTO signals invalid
        }

        long now = System.currentTimeMillis();
        long loginTime = sessionEntity.getLoginDateTime().getTime();
        long elapsedMinutes = (now - loginTime) / (1000 * 60);

        if (elapsedMinutes > 30) {
            NewCookie deleteCookie = new NewCookie("session_id",
                    null, "/", null,
                    "Expired session",
                    0, false);
            return Response.ok(new UserSessionDTO()).cookie(deleteCookie).build();
        }

        // Valid session — return DTO
        return Response.ok(converter.convert(sessionEntity)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSession(@PathParam("id") int id) {
        boolean deleted = userSessionDAO.deleteSessionById(id);
        if (deleted) {
            LOGGER.info("Deleted user session with id {}", id);
            NewCookie expiredCookie = new NewCookie("session_id", null, "/", null, "Session deleted", 0, false);
            return Response.noContent().cookie(expiredCookie).build();
        } else {
            LOGGER.warn("No session found with id {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Session not found\"}")
                    .build();
        }
    }
}
