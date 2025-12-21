package com.really.good.sir.resources;

import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.ServiceDTO;
import com.really.good.sir.dto.UserSessionDTO;
import com.really.good.sir.entity.Role;
import com.really.good.sir.service.ServiceService;
import com.really.good.sir.service.UserSessionService;
import com.really.good.sir.validator.ServiceValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/services")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServiceResource {
    private static final Logger LOGGER = LogManager.getLogger(ServiceResource.class);
    private final ServiceService service = new ServiceService();
    private final UserSessionService userSessionService = new UserSessionService();
    private final ServiceValidator serviceValidator = new ServiceValidator();

    @GET
    public Response getAllServices(@CookieParam("session_id") final String sessionId) {
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

            UserSessionDTO session = userSessionService.getSessionById(sessionIdInt);
            if (session == null) {
                LOGGER.error("Session id does not exist [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole()) &&
                    !Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to admin or call center agent role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }
            List<ServiceDTO> dtos = service.getAllServices();
            return Response.ok(dtos).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get all services", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get all services");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getServiceById(@PathParam("id") Integer id,
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

            UserSessionDTO session = userSessionService.getSessionById(sessionIdInt);
            if (session == null) {
                LOGGER.error("Session id does not exist [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole()) &&
                    !Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to admin or call center agent role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (serviceValidator.isEmpty(id)) {
                LOGGER.error("Service id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!serviceValidator.exists(id)) {
                LOGGER.error("Service id does not exist");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            ServiceDTO dto = service.getServiceById(id);
            if (dto == null) {
                LOGGER.error("Service was not found");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service was not found");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.ok(dto).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get service by id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get service by id");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @POST
    public Response createService(ServiceDTO dto,
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

            UserSessionDTO session = userSessionService.getSessionById(sessionIdInt);
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

            if (!serviceValidator.isNameValid(dto)) {
                LOGGER.error("Service name has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service name has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!serviceValidator.isNameUnique(dto)) {
                LOGGER.error("Service name already exists");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service name already exists");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!serviceValidator.isPriceValid(dto)) {
                LOGGER.error("Price has to be over 0");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Price has to be over 0");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            ServiceDTO created = service.createService(dto);
            if (created == null) {
                LOGGER.error("Service is not created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service is not created");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.ok(created).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to create new service", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to create new service");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @PUT
    public Response updateService(ServiceDTO dto,
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

            UserSessionDTO session = userSessionService.getSessionById(sessionIdInt);
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

            if (!serviceValidator.isNameValid(dto)) {
                LOGGER.error("Service name has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service name has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!serviceValidator.isNameUnique(dto)) {
                LOGGER.error("Service name already exists");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service name already exists");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!serviceValidator.isPriceValid(dto)) {
                LOGGER.error("Price has to be over 0");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Price has to be over 0");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            ServiceDTO result = service.updateService(dto);
            if (result == null) {
                LOGGER.error("Service is not updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service is not updated");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.ok(result).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to update existing service", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to update existing service");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteService(@PathParam("id") Integer id,
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

            UserSessionDTO session = userSessionService.getSessionById(sessionIdInt);
            if (session == null) {
                LOGGER.error("Session id does not exist [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole()) &&
                    !Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to admin or call center agent role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }
            if (serviceValidator.isEmpty(id)) {
                LOGGER.error("Service id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No doctor id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!serviceValidator.exists(id)) {
                LOGGER.error("Service id does not exist");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            boolean deleted = service.deleteService(id);
            if (!deleted) {
                LOGGER.error("Service is not deleted");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service is not deleted");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.noContent().build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to delete service by id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to delete service by id");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }
}
