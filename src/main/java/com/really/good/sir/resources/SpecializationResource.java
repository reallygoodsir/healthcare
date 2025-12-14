package com.really.good.sir.resources;

import com.really.good.sir.converter.SpecializationConverter;
import com.really.good.sir.dao.SpecializationDAO;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.SpecializationDTO;
import com.really.good.sir.entity.Role;
import com.really.good.sir.entity.SpecializationEntity;
import com.really.good.sir.entity.UserSessionEntity;
import com.really.good.sir.validator.SpecializationValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/specializations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SpecializationResource {
    private static final Logger LOGGER = LogManager.getLogger(DoctorResource.class);
    private final SpecializationConverter specializationConverter = new SpecializationConverter();
    private final SpecializationDAO specializationDAO = new SpecializationDAO();
    private final UserSessionDAO userSessionDAO = new UserSessionDAO();

    private final SpecializationValidator specializationValidator = new SpecializationValidator();

    @GET
    public Response getAllSpecializations(@CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is empty");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            int sessionIdInt;
            try {
                sessionIdInt = Integer.parseInt(sessionId);
            } catch (NumberFormatException exception) {
                LOGGER.error("Session id is not valid", exception);
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id has incorrect format");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
            if (session == null) {
                LOGGER.error("Session id does not exist [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to admin role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }
            final List<SpecializationEntity> specializationEntities = specializationDAO.getAllSpecializations();
            final List<SpecializationDTO> specializationDTOs = specializationConverter.convert(specializationEntities);
            return Response.ok(specializationDTOs).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get all specializations", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get all specializations");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    @Path("/{specializationId}")
    public Response getSpecializationById(@PathParam("specializationId") final int specializationId,
                                          @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is empty");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            int sessionIdInt;
            try {
                sessionIdInt = Integer.parseInt(sessionId);
            } catch (NumberFormatException exception) {
                LOGGER.error("Session id is not valid", exception);
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id has incorrect format");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
            if (session == null) {
                LOGGER.error("Session id does not exist [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole()) &&
                    !Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to admin or call center agent role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (specializationValidator.isEmpty(specializationId)) {
                LOGGER.error("Specialization id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No specialization id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!specializationValidator.exists(specializationId)) {
                LOGGER.error("Specialization id does not exist");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Specialization id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            final SpecializationEntity specializationEntity = specializationDAO.getSpecializationById(specializationId);
            if (specializationEntity == null) {
                LOGGER.error("Specialization was not found");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Specialization was not found");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            final SpecializationDTO specializationDTO = specializationConverter.convert(specializationEntity);
            return Response.ok(specializationDTO).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get specialization by id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get specialization by id");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }
}
