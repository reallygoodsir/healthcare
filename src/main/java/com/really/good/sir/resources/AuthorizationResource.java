package com.really.good.sir.resources;

import com.really.good.sir.converter.UserSessionConverter;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.LoginRequestDTO;
import com.really.good.sir.dto.SessionCheckRequestDTO;
import com.really.good.sir.dto.UserSessionDTO;
import com.really.good.sir.entity.UserSessionEntity;
import com.really.good.sir.service.UserSessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/authorization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorizationResource {
    private static final Logger LOGGER = LogManager.getLogger(AuthorizationResource.class);
    private final UserSessionService userSessionService = new UserSessionService();
    private final UserSessionConverter converter = new UserSessionConverter();

    @POST
    public Response authorize(LoginRequestDTO request) {
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        try {
            if (request == null) {
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Login request is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Password is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            UserSessionDTO sessionDTO = userSessionService.authorize(request.getEmail(), request.getPassword());
            if (sessionDTO == null) {
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Credentials are not valid");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }
            NewCookie cookie = new NewCookie("session_id",
                    String.valueOf(sessionDTO.getId()),
                    "/", null,
                    "User session ID",
                    30 * 60, false);

            LOGGER.info("User logged in with credential_id {} and role {}", sessionDTO.getCredentialId(), sessionDTO.getRole());

            return Response.ok(sessionDTO).cookie(cookie).build();
        } catch (Exception exception) {
            LOGGER.error("Error trying to authorize the user", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to authorize the user");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @POST
    @Path("/session")
    public Response checkSession(SessionCheckRequestDTO request) {
        try {
            if (request == null) {
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session request is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (request.getSessionId() == null) {
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            UserSessionDTO sessionEntity = userSessionService.getSessionById(request.getSessionId());
            if (sessionEntity == null) {
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is not valid");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
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

            return Response.ok(converter.convert(sessionEntity)).build();
        } catch (Exception exception) {
            LOGGER.error("Error trying to check the session", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to check the session");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @DELETE
    @Path("/session/{sessionId}")
    public Response deleteSession(@PathParam("sessionId") Integer sessionId) {
        try {
            if (sessionId == null) {
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            UserSessionDTO sessionEntity = userSessionService.getSessionById(sessionId);
            if (sessionEntity == null) {
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is not valid");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            boolean deleted = userSessionService.deleteSessionById(sessionId);
            if (!deleted) {
                LOGGER.error("Session was not deleted with id {}", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session was not deleted");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }

            LOGGER.info("Deleted user session with id {}", sessionId);
            NewCookie expiredCookie = new NewCookie("session_id",
                    null,
                    "/",
                    null,
                    "Session deleted",
                    0,
                    false);
            return Response.noContent().cookie(expiredCookie).build();
        } catch (Exception exception) {
            LOGGER.error("Error trying to delete th user", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to delete th user");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }
}
