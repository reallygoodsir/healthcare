package com.really.good.sir.resources;

import com.really.good.sir.converter.PatientAppointmentOutcomeConverter;
import com.really.good.sir.dao.PatientAppointmentOutcomeDAO;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.PatientAppointmentOutcomeDTO;
import com.really.good.sir.entity.PatientAppointmentOutcomeEntity;
import com.really.good.sir.entity.Role;
import com.really.good.sir.entity.UserSessionEntity;
import com.really.good.sir.validator.PatientAppointmentOutcomeValidator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/patient-appointments/outcome")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientAppointmentOutcomeResource {

    private final PatientAppointmentOutcomeDAO dao = new PatientAppointmentOutcomeDAO();
    private final UserSessionDAO userSessionDAO = new UserSessionDAO();
    private final PatientAppointmentOutcomeValidator outcomeValidator = new PatientAppointmentOutcomeValidator();
    private final PatientAppointmentOutcomeConverter converter = new PatientAppointmentOutcomeConverter();

    @GET
    @Path("/{appointmentId}")
    public Response getOutcome(@PathParam("appointmentId") int appointmentId, @CookieParam("session_id") final String sessionId) {
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
        PatientAppointmentOutcomeEntity entity = dao.getOutcomeByAppointmentId(appointmentId);
        PatientAppointmentOutcomeDTO dto = converter.convert(entity);
        if (dto != null) {
            return Response.ok(dto).build();
        }
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage("Not found");
        return Response.status(Response.Status.NOT_FOUND)
                .entity(errorDTO)
                .build();
    }

    @PUT
    @Path("/{appointmentId}")
    public Response saveOrUpdateOutcome(@PathParam("appointmentId") int appointmentId, PatientAppointmentOutcomeDTO dto, @CookieParam("session_id") final String sessionId) {
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

        if (!outcomeValidator.isResultValid(dto)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Result can't be empty");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        try {
            dto.setAppointmentId(appointmentId);
            PatientAppointmentOutcomeEntity entity = dao.saveOrUpdateOutcome(dto);
            PatientAppointmentOutcomeDTO result = converter.convert(entity);
            return Response.ok(result).build();
        } catch (IllegalStateException ise) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Bad request | Illegal State Exception");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();

        } catch (Exception e) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Failed to save or update outcome");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }
}
