package com.really.good.sir.resources;

import com.really.good.sir.dao.AppointmentDAO;
import com.really.good.sir.dao.AppointmentOutcomeDAO;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.AppointmentDTO;
import com.really.good.sir.dto.AppointmentOutcomeDTO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.entity.AppointmentEntity;
import com.really.good.sir.converter.AppointmentConverter;
import com.really.good.sir.entity.Role;
import com.really.good.sir.entity.UserSessionEntity;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/appointments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppointmentResource {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final AppointmentOutcomeDAO outcomeDAO = new AppointmentOutcomeDAO();
    private final AppointmentConverter converter = new AppointmentConverter();
    private final UserSessionDAO userSessionDAO = new UserSessionDAO();

    @POST
    public Response createAppointment(AppointmentDTO dto, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
            dto.setStatus("SCHEDULED");
        }
        AppointmentEntity entity = converter.convert(dto);
        AppointmentEntity created = appointmentDAO.createAppointment(entity);
        return Response.ok(converter.convert(created)).build();
    }

    @GET
    public Response getAllAppointments(@CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole()) && !Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        List<AppointmentEntity> appointments = appointmentDAO.getAllAppointments();
        return Response.ok(converter.convert(appointments)).build();
    }

    @GET
    @Path("/{appointmentId}")
    public Response getAppointmentById(@PathParam("appointmentId") int appointmentId, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        AppointmentEntity entity = appointmentDAO.getAppointmentById(appointmentId);
        if (entity != null) {
            return Response.ok(converter.convert(entity)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // --- New endpoint to get outcome separately ---
    @GET
    @Path("/{appointmentId}/outcome")
    public Response getAppointmentOutcome(@PathParam("appointmentId") int appointmentId, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }

        AppointmentOutcomeDTO outcome = outcomeDAO.getOutcomeByAppointmentId(appointmentId);
        if (outcome != null) {
            return Response.ok(outcome).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{appointmentId}")
    public Response updateAppointmentOutcome(@PathParam("appointmentId") int appointmentId, AppointmentOutcomeDTO dto, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        try {
            dto.setAppointmentId(appointmentId);
            AppointmentOutcomeDTO updated = outcomeDAO.saveOrUpdateOutcome(dto);
            return Response.ok(updated).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to save outcome").build();
        }
    }

    @PATCH
    @Path("/{appointmentId}/status")
    public Response updateStatus(@PathParam("appointmentId") int appointmentId, @QueryParam("status") String status, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        boolean success = appointmentDAO.updateAppointmentStatus(appointmentId, status);
        if (success) return Response.noContent().build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{appointmentId}")
    public Response deleteAppointment(@PathParam("appointmentId") int appointmentId, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        boolean deleted = appointmentDAO.deleteAppointment(appointmentId);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
