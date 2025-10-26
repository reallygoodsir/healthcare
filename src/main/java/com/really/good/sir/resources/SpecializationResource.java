package com.really.good.sir.resources;

import com.really.good.sir.converter.SpecializationConverter;
import com.really.good.sir.dao.SpecializationDAO;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.SpecializationDTO;
import com.really.good.sir.entity.Role;
import com.really.good.sir.entity.SpecializationEntity;
import com.really.good.sir.entity.UserSessionEntity;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/specializations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SpecializationResource {
    private final SpecializationConverter specializationConverter = new SpecializationConverter();
    private final SpecializationDAO specializationDAO = new SpecializationDAO();
    private final UserSessionDAO userSessionDAO = new UserSessionDAO();

    @GET
    public Response getAllSpecializations(@CookieParam("session_id") final String sessionId) {
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
        final List<SpecializationEntity> specializationEntities = specializationDAO.getAllSpecializations();
        final List<SpecializationDTO> specializationDTOs = specializationConverter.convert(specializationEntities);
        return Response.ok(specializationDTOs).build();
    }

    @GET
    @Path("/{specializationId}")
    public Response getSpecializationById(@PathParam("specializationId") final int specializationId, @CookieParam("session_id") final String sessionId) {
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
        final SpecializationEntity specializationEntity = specializationDAO.getSpecializationById(specializationId);
        final SpecializationDTO specializationDTO = specializationConverter.convert(specializationEntity);
        return Response.ok(specializationDTO).build();
    }
}
