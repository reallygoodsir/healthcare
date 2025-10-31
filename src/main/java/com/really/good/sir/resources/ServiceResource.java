package com.really.good.sir.resources;

import com.really.good.sir.converter.ServiceConverter;
import com.really.good.sir.dao.ServiceDAO;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.ServiceDTO;
import com.really.good.sir.entity.Role;
import com.really.good.sir.entity.ServiceEntity;
import com.really.good.sir.entity.UserSessionEntity;
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

    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final ServiceConverter serviceConverter = new ServiceConverter();
    private final UserSessionDAO userSessionDAO = new UserSessionDAO();
    private final ServiceValidator serviceValidator = new ServiceValidator();

    @GET
    public Response getAllServices(@CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole()) && !Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        List<ServiceEntity> entities = serviceDAO.getAllServices();
        List<ServiceDTO> dtos = serviceConverter.convert(entities);
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{id}")
    public Response getServiceById(@PathParam("id") int id, @CookieParam("session_id") final String sessionId) {
        ServiceEntity entity = serviceDAO.getServiceById(id);
        if (entity == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(serviceConverter.convert(entity)).build();
    }

    @POST
    public Response createService(ServiceDTO dto, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }

        ServiceEntity entity = serviceConverter.convert(dto);
        if (!serviceValidator.isNameValid(dto)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Name has the wrong format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!serviceValidator.isNameUnique(dto)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Name already exists");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!serviceValidator.isPriceValid(dto)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Price has to be over 0");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }
        ServiceEntity created = serviceDAO.createService(entity);
        return Response.ok(serviceConverter.convert(created)).build();
    }

    @PUT
    public Response updateService(ServiceDTO dto, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }

        ServiceEntity entity = serviceConverter.convert(dto);
        if (!serviceValidator.isNameValid(dto)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Name has the wrong format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!serviceValidator.isNameUnique(dto)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Name already exists");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!serviceValidator.isPriceValid(dto)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Price has to be over 0");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        boolean updated = serviceDAO.updateService(entity);
        LOGGER.info("Service updated: {}", updated);
        return Response.ok(serviceConverter.convert(entity)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteService(@PathParam("id") int id, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        boolean deleted = serviceDAO.deleteService(id);
        LOGGER.info("Service deleted: {}", deleted);
        return Response.noContent().build();
    }
}
