package com.really.good.sir.resources;

import com.really.good.sir.converter.AppointmentOutcomeConverter;
import com.really.good.sir.dao.AppointmentDAO;
import com.really.good.sir.dao.AppointmentOutcomeDAO;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.AppointmentDTO;
import com.really.good.sir.dto.AppointmentOutcomeDTO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.entity.AppointmentEntity;
import com.really.good.sir.converter.AppointmentConverter;
import com.really.good.sir.entity.AppointmentOutcomeEntity;
import com.really.good.sir.entity.Role;
import com.really.good.sir.entity.UserSessionEntity;
import com.really.good.sir.validator.AppointmentOutcomeValidator;
import com.really.good.sir.validator.AppointmentValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/appointments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppointmentResource {
    private static final Logger LOGGER = LogManager.getLogger(AppointmentResource.class);
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final AppointmentOutcomeDAO outcomeDAO = new AppointmentOutcomeDAO();
    private final AppointmentConverter converter = new AppointmentConverter();
    private final AppointmentOutcomeConverter outcomeConverter = new AppointmentOutcomeConverter();
    private final UserSessionDAO userSessionDAO = new UserSessionDAO();
    private final AppointmentOutcomeValidator patientValidator = new AppointmentOutcomeValidator();
    private final AppointmentValidator appointmentValidator = new AppointmentValidator();

    @POST
    public Response createAppointment(final AppointmentDTO appointmentDTO, @CookieParam("session_id") final String sessionId) {
        LOGGER.info("Start to create appointment. Session id [{}]. Appointment request[{}]", sessionId, appointmentDTO);
        try {
            // SECURITY CHECK
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            int sessionIdInt;
            try {
                sessionIdInt = Integer.parseInt(sessionId);
            } catch (NumberFormatException e) {
                LOGGER.error("Session id is not valid", e);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id has incorrect format.");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
            if (session == null) {
                LOGGER.error("Session id does not exist [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist.");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to call center agent role [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            // REQUEST VALIDATION

            // if AppointmentDTO has appointmentId with value then return bad request
            if (!appointmentValidator.isAppointmentIdValid(appointmentDTO)) {
                LOGGER.error("The appointment request has a preexisting appointment id [{}]", appointmentDTO.getAppointmentId());
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("The appointment should have no preexisting appointment id");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            // if AppointmentDTO has status with value then return bad request
            if (!appointmentValidator.isStatusValid(appointmentDTO)) {
                LOGGER.error("The appointment request has a preexisting status [{}]", appointmentDTO.getStatus());
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("The appointment should have no preexisting status");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            // check if patientId is null or empty then return bad request
            if (appointmentValidator.isPatientIdEmpty(appointmentDTO)) {
                LOGGER.error("The appointment request does not have a patient id");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No patient id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            // check if patientId exists and if not then return bad request
            if (appointmentValidator.isPatientIdInvalid(appointmentDTO)) {
                LOGGER.error("The appointment request patient id is incorrect");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Incorrect patient id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            // check if doctorId is null or empty then return bad request
            if (appointmentValidator.isDoctorIdEmpty(appointmentDTO)) {
                LOGGER.error("The appointment request does not have doctor id");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No doctor id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            // check if doctorId exists and if not then return bad request
            if (appointmentValidator.isDoctorIdInvalid(appointmentDTO)) {
                LOGGER.error("The appointment request doctor id is incorrect");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Incorrect doctor id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            // check if scheduleId is null or empty then return bad request
            if (appointmentValidator.isScheduleIdEmpty(appointmentDTO)) {
                LOGGER.error("The appointment request doesn't have a schedule id");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No schedule id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            // check if scheduleId exists and if not then return bad request
            if (!appointmentValidator.isScheduleIdValid(appointmentDTO)) {
                LOGGER.error("The appointment request schedule id is incorrect");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Incorrect schedule id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            appointmentDTO.setStatus("SCHEDULED");

            // REQUEST CONVERSION TO DB ENTITY
            AppointmentEntity entity = converter.convert(appointmentDTO);

            // SAVE ENTITY TO DB
            AppointmentEntity created = appointmentDAO.createAppointment(entity);
            if (created == null) {
                LOGGER.error("Failed to create an appointment [{}]", appointmentDTO);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Failed to create an appointment");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.ok(converter.convert(created)).build();
        } catch (final Exception exception) {
            LOGGER.error("Error during appointment creation [{}]", appointmentDTO, exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error during appointment creation");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
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
        final ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage("Bad request");
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorDTO)
                .build();
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

        AppointmentOutcomeEntity outcomeByAppointmentId = outcomeDAO.getOutcomeByAppointmentId(appointmentId);
        AppointmentOutcomeDTO outcome = outcomeConverter.convert(outcomeByAppointmentId);
        if (outcome != null) {
            return Response.ok(outcome).build();
        }
        final ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage("Bad request");
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorDTO)
                .build();
    }

    @PUT
    @Path("/{appointmentId}")
    public Response updateAppointmentOutcome(@PathParam("appointmentId") int appointmentId, AppointmentOutcomeDTO dto, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }

        int sessionIdInt;
        try {
            sessionIdInt = Integer.parseInt(sessionId);
        } catch (NumberFormatException e) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }

        UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
        if (session == null || !Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        if (!patientValidator.isDiagnosisValid(dto)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("No diagnosis provided");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isRecommendationsValid(dto)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("No recommendation provided");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }
        try {
            dto.setAppointmentId(appointmentId);
            AppointmentOutcomeEntity updated = outcomeDAO.saveOrUpdateOutcome(dto);
            AppointmentOutcomeDTO outcome = outcomeConverter.convert(updated);
            return Response.ok(outcome).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to save outcome").build();
        }
    }

    @PATCH
    @Path("/{appointmentId}/{status}")
    public Response updateStatus(@PathParam("appointmentId") int appointmentId, @PathParam("status") String status, @CookieParam("session_id") final String sessionId) {
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

        final ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage("Status couldn't be updated");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorDTO)
                .build();
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

        int sessionIdInt;
        try {
            sessionIdInt = Integer.parseInt(sessionId);
        } catch (NumberFormatException e) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }

        UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
        if (session == null || !Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
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
        final ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage("Bad request");
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorDTO)
                .build();
    }
}
