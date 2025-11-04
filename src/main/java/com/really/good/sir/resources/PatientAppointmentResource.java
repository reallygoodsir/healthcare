package com.really.good.sir.resources;

import com.really.good.sir.converter.PatientAppointmentConverter;
import com.really.good.sir.dao.PatientAppointmentDAO;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.PatientAppointmentDTO;
import com.really.good.sir.dto.PatientAppointmentDetailsDTO;
import com.really.good.sir.entity.PatientAppointmentEntity;
import com.really.good.sir.entity.Role;
import com.really.good.sir.entity.UserSessionEntity;
import com.really.good.sir.validator.PatientAppointmentValidator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/patient-appointments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientAppointmentResource {

    private final PatientAppointmentDAO dao = new PatientAppointmentDAO();
    private final PatientAppointmentConverter converter = new PatientAppointmentConverter();
    private final UserSessionDAO userSessionDAO = new UserSessionDAO();
    private final PatientAppointmentValidator patientAppointmentValidator = new PatientAppointmentValidator();

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
        if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        List<PatientAppointmentEntity> list = dao.getAllAppointments();
        List<PatientAppointmentDTO> dtos = list.stream().map(converter::convert).toList();
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{doctorId}")
    public Response getAppointmentsByDoctor(@PathParam("doctorId") int doctorId, @CookieParam("session_id") final String sessionId) {
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
        List<PatientAppointmentEntity> list = dao.getAppointmentsByDoctorId(doctorId);
        List<PatientAppointmentDTO> dtos = list.stream().map(converter::convert).toList();
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/today/{doctorId}")
    public Response getTodaysAppointmentsByDoctor(@PathParam("doctorId") int doctorId, @CookieParam("session_id") final String sessionId) {
        List<PatientAppointmentEntity> list = dao.getTodaysAppointmentsByDoctor(doctorId);
        List<PatientAppointmentDTO> dtos = list.stream().map(converter::convert).toList();
        return Response.ok(dtos).build();
    }

    @POST
    public Response createAppointment(PatientAppointmentDTO dto, @CookieParam("session_id") final String sessionId) {
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

        if (!patientAppointmentValidator.isDateValid(dto)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Date must be not be in the past");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientAppointmentValidator.isTimeRangeValid(dto)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Invalid start/end time");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (patientAppointmentValidator.isOverlapping(dto)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Time overlaps with an existing schedule");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        PatientAppointmentEntity entity = converter.convert(dto);
        PatientAppointmentEntity created = dao.createAppointment(entity);
        return Response.ok(converter.convert(created)).build();
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
        boolean deleted = dao.deleteAppointment(appointmentId);
        return deleted ? Response.noContent().build() : Response.status(500).build();
    }

    @PATCH
    @Path("/{appointmentId}/status")
    public Response updateStatus(@PathParam("appointmentId") int appointmentId,
                                 @QueryParam("status") String status, @CookieParam("session_id") final String sessionId) {
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
        if (status == null || status.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Status query parameter is required").build();
        }
        boolean updated = dao.updateStatus(appointmentId, status);
        if (updated) return Response.noContent().build();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Failed to update appointment status").build();
    }

    @GET
    @Path("/status/{appointmentId}")
    public Response getAppointmentStatus(@PathParam("appointmentId") int appointmentId, @CookieParam("session_id") final String sessionId) {
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
        String status = dao.getAppointmentStatusById(appointmentId);
        if (status == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Appointment not found")).build();
        }
        return Response.ok(Map.of("status", status)).build();
    }

    @GET
    @Path("/patient/{patientId}/details")
    public Response getAppointmentDetailsByPatient(@PathParam("patientId") int patientId,
                                                   @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            ErrorDTO error = new ErrorDTO();
            error.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
        }

        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        String role = session.getRole();

        if (!Role.PATIENT.toString().equalsIgnoreCase(role)) {
            ErrorDTO error = new ErrorDTO();
            error.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(error)
                    .build();
        }

        List<PatientAppointmentDetailsDTO> list = dao.getAppointmentDetailsByPatientId(patientId);
        return Response.ok(list).build();
    }

}
