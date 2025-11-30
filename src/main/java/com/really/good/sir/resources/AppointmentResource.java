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
            if (!appointmentValidator.isAppointmentIdEmpty(appointmentDTO)) {
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

    @PUT
    public Response updateAppointmentOutcome(final AppointmentOutcomeDTO appointmentOutcomeDTO, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            LOGGER.error("Session id is empty");
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
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
            errorDTO.setMessage("Not authorized. Session id does not exist.");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }

        if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
            LOGGER.error("Session id does not belong to doctor role [{}]", sessionId);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }

        if (patientValidator.isAppointmentIdEmpty(appointmentOutcomeDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("No appointment id provided");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!appointmentValidator.isAppointmentIdValid(appointmentOutcomeDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Incorrect appointment id provided");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isDiagnosisValid(appointmentOutcomeDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("No diagnosis provided");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isRecommendationsValid(appointmentOutcomeDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("No recommendation provided");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        try {
            AppointmentOutcomeEntity entity = outcomeConverter.convert(appointmentOutcomeDTO);
            AppointmentOutcomeEntity entityResponse = outcomeDAO.saveOrUpdateOutcome(entity);
            AppointmentOutcomeDTO outcome = outcomeConverter.convert(entityResponse);
            return Response.ok(outcome).build();
        } catch (Exception exception) {
            LOGGER.error("Error during appointment outcome processing [{}]", appointmentOutcomeDTO, exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error during appointment outcome processing");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    public Response getAllAppointments(@CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized");
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
                LOGGER.error("Session id does not exist [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist.");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole()) && !Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to doctor/call center agent role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }
            List<AppointmentEntity> appointments = appointmentDAO.getAllAppointments(); // an empty response is fine
            return Response.ok(converter.convert(appointments)).build();
        } catch (Exception exception) {
            LOGGER.error("Error while getting all appointments", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error while getting all appointments");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    @Path("/{appointmentId}")
    public Response getAppointmentById(@PathParam("appointmentId") int appointmentId, @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized");
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
                errorDTO.setMessage("Not authorized. Session id does not exist.");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to doctor role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (appointmentValidator.isAppointmentIdEmpty(appointmentId)) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No appointment id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            AppointmentEntity entity = appointmentDAO.getAppointmentById(appointmentId);
            if (entity == null) {
                LOGGER.error("Failed to get an appointment using id [{}]", appointmentId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Failed to get an appointment by id");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.ok(converter.convert(entity)).build();
        } catch (Exception exception) {
            LOGGER.error("Error while getting an appointment using id [{}]", appointmentId, exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error while getting an appointment by id");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    // doctor
    @GET
    @Path("/{appointmentId}/outcome")
    public Response getAppointmentOutcome(@PathParam("appointmentId") int appointmentId, @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized");
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
                errorDTO.setMessage("Not authorized. Session id does not exist.");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to doctor role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (appointmentValidator.isAppointmentIdEmpty(appointmentId)) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No appointment id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            AppointmentOutcomeEntity entity = outcomeDAO.getOutcomeByAppointmentId(appointmentId);
            if (entity == null) {
                LOGGER.error("Failed to get an appointment outcome [{}]", appointmentId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Failed to get an appointment outcome");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }

            AppointmentOutcomeDTO outcome = outcomeConverter.convert(entity);
            return Response.ok(outcome).build();
        } catch (Exception exception) {
            LOGGER.error("Error while getting an appointment outcome for id [{}]", appointmentId, exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error while getting an appointment outcome");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @PATCH
    @Path("/{appointmentId}/{status}")
    public Response updateStatus(@PathParam("appointmentId") int appointmentId, @PathParam("status") String status, @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized");
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
                errorDTO.setMessage("Not authorized. Session id does not exist.");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to doctor role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (appointmentValidator.isAppointmentIdEmpty(appointmentId)) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No appointment id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!appointmentValidator.isAppointmentIdValid(appointmentId)) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment id not found");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (appointmentValidator.isStatusEmpty(status)) {
                LOGGER.error("No status provided");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No status provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!appointmentValidator.isStatusValid(status)) {
                LOGGER.error("Invalid status provided");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Invalid status provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            boolean success = appointmentDAO.updateAppointmentStatus(appointmentId, status);

            if (success) return Response.noContent().build();

            LOGGER.error("Failed to update status [{}] for id [{}]", status, appointmentId);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Failed to update status");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();


        } catch (Exception exception) {
            LOGGER.error("Error while updating status [{}] for id [{}]", status, appointmentId, exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error while updating status");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @DELETE
    @Path("/{appointmentId}")
    public Response deleteAppointment(@PathParam("appointmentId") int appointmentId, @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized");
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
                errorDTO.setMessage("Not authorized. Session id does not exist.");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to call center agent role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (appointmentValidator.isAppointmentIdEmpty(appointmentId)) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No appointment id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!appointmentValidator.isAppointmentIdValid(appointmentId)) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment id not found");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            boolean deleted = appointmentDAO.deleteAppointment(appointmentId);
            if (!deleted) {
                LOGGER.error("Failed to delete an appointment [{}]", appointmentId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Failed to delete an appointment");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.noContent().build();
        } catch (Exception exception) {
            LOGGER.error("Error while deleting appointment with id [{}]", appointmentId, exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error while updating status");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }
}
