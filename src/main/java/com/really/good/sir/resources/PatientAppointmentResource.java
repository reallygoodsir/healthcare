package com.really.good.sir.resources;

import com.really.good.sir.dto.*;
import com.really.good.sir.entity.Role;
import com.really.good.sir.service.PatientAppointmentOutcomeService;
import com.really.good.sir.service.PatientAppointmentService;
import com.really.good.sir.service.UserSessionService;
import com.really.good.sir.validator.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/patient-appointments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientAppointmentResource {
    private static final Logger LOGGER = LogManager.getLogger(PatientAppointmentResource.class);
    private final PatientAppointmentService patientAppointmentService = new PatientAppointmentService();
    private final PatientAppointmentOutcomeService patientAppointmentOutcomeService = new PatientAppointmentOutcomeService();
    private final UserSessionService userSessionService = new UserSessionService();
    private final PatientAppointmentValidator patientAppointmentValidator = new PatientAppointmentValidator();
    private final AppointmentValidator appointmentValidator = new AppointmentValidator();
    private final DoctorValidator doctorValidator = new DoctorValidator();
    private final PatientValidator patientValidator = new PatientValidator();
    private final PatientAppointmentOutcomeValidator outcomeValidator = new PatientAppointmentOutcomeValidator();
    private final ServiceValidator serviceValidator = new ServiceValidator();

    @GET
    public Response getAllAppointments(@CookieParam("session_id") final String sessionId) {
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

            if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to call center agent role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }
            List<PatientAppointmentDTO> result = patientAppointmentService.getAllAppointments();
            return Response.ok(result).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get all appointments", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get all appointments");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    @Path("/{doctorId}")
    public Response getAppointmentsByDoctorId(@PathParam("doctorId") int doctorId,
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

            if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to doctor role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (doctorValidator.isIdEmpty(doctorId)) {
                LOGGER.error("Doctor id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No doctor id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.idExists(doctorId)) {
                LOGGER.error("Doctor id does not exist");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            List<PatientAppointmentDTO> result = patientAppointmentService.getAppointmentsByDoctorId(doctorId);
            return Response.ok(result).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get appointments by doctor id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get appointments by doctor id");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    @Path("/{doctorId}/{date}")
    public Response getAppointmentsByDoctorIdAndDate(@PathParam("doctorId") Integer doctorId,
                                                     @PathParam("date") String date,
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

            if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to doctor role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (doctorValidator.isIdEmpty(doctorId)) {
                LOGGER.error("Doctor id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No doctor id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.idExists(doctorId)) {
                LOGGER.error("Doctor id does not exist");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!appointmentValidator.isAppointmentDateValid(date)) {
                LOGGER.error("Appointment date is not valid");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment date is not valid");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            List<PatientAppointmentDTO> result = patientAppointmentService.getTodaysAppointmentsByDoctor(doctorId);
            return Response.ok(result).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get doctor appointments for specific date", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get doctor appointments for specific date");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @POST
    public Response createAppointment(final PatientAppointmentDTO dto,
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

            if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to call center agent role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientAppointmentValidator.isIdEmpty(dto.getAppointmentId())) {
                LOGGER.error("Appointment id must be empty when new appointment is created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment id must be empty when new appointment is created");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (doctorValidator.isIdEmpty(dto.getDoctorId())) {
                LOGGER.error("Doctor id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.idExists(dto.getDoctorId())) {
                LOGGER.error("Doctor id does not exist");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (patientValidator.isPatientIdEmpty(dto.getPatientId())) {
                LOGGER.error("Patient id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isPatientIdExists(dto.getPatientId())) {
                LOGGER.error("Patient id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (serviceValidator.isEmpty(dto.getServiceId())) {
                LOGGER.error("Service id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!serviceValidator.exists(dto.getServiceId())) {
                LOGGER.error("Service id does not exist");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }


            if (!patientAppointmentValidator.isDateValid(dto)) {
                LOGGER.error("Date must not be in the past");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Date must not be in the past");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientAppointmentValidator.isTimeRangeValid(dto)) {
                LOGGER.error("Invalid start/end time");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Invalid start/end time");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (patientAppointmentValidator.isOverlapping(dto)) {
                LOGGER.error("Time overlaps with an existing schedule");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Time overlaps with an existing schedule");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            PatientAppointmentDTO created = patientAppointmentService.createAppointment(dto);
            if (created == null) {
                LOGGER.error("Appointment is not created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment is not created");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.ok(created).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to create appointment", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to create appointment");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @PATCH
    @Path("/{appointmentId}/{status}")
    public Response updateStatus(@PathParam("appointmentId") Integer appointmentId,
                                 @PathParam("status") String status,
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

            if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to doctor role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (patientAppointmentValidator.isIdEmpty(appointmentId)) {
                LOGGER.error("Appointment id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientAppointmentValidator.isIdExists(appointmentId)) {
                LOGGER.error("Appointment id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (status == null || status.isBlank()) {
                LOGGER.error("Appointment status is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment status is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();

            }

            if (!status.equalsIgnoreCase("SCHEDULED") &&
                    !status.equalsIgnoreCase("COMPLETED")) {
                LOGGER.error("Appointment status has incorrect value");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment status has incorrect value");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            boolean updated = patientAppointmentService.updateStatus(appointmentId, status);
            if (!updated) {
                LOGGER.error("Appointment status is not updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment status is not updated");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.noContent().build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to update appointment status", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to update appointment status");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @DELETE
    @Path("/{appointmentId}")
    public Response deleteAppointment(@PathParam("appointmentId") Integer appointmentId,
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

            if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to call center agent role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (patientAppointmentValidator.isIdEmpty(appointmentId)) {
                LOGGER.error("Appointment id must not be empty when existing appointment is deleted");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id must not be empty when existing appointment is deleted");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientAppointmentValidator.isIdExists(appointmentId)) {
                LOGGER.error("Appointment id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            boolean deleted = patientAppointmentService.deleteAppointment(appointmentId);
            if (!deleted) {
                LOGGER.error("Appointment is not deleted");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment is not deleted");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.noContent().build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to delete appointment", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to delete appointment");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }


    @GET
    @Path("/status/{appointmentId}")
    public Response getAppointmentStatus(@PathParam("appointmentId") Integer appointmentId,
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

            if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to doctor role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (patientAppointmentValidator.isIdEmpty(appointmentId)) {
                LOGGER.error("Appointment id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientAppointmentValidator.isIdExists(appointmentId)) {
                LOGGER.error("Appointment id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            String status = patientAppointmentService.getAppointmentStatusById(appointmentId);
            if (status == null || status.isBlank()) {
                LOGGER.error("Appointment status not found");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment status not found");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            AppointmentDTO appointmentDTO = new AppointmentDTO();
            appointmentDTO.setAppointmentId(appointmentId);
            appointmentDTO.setStatus(status);
            return Response.ok(appointmentDTO).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get appointment status", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get appointment status");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    @Path("/patient/{patientId}/details")
    public Response getAppointmentDetailsByPatient(@PathParam("patientId") Integer patientId,
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

            String role = session.getRole();
            if (!Role.PATIENT.toString().equalsIgnoreCase(role)) {
                LOGGER.error("Session id does not belong to patient role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (patientValidator.isPatientIdEmpty(patientId)) {
                LOGGER.error("Patient id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isPatientIdExists(patientId)) {
                LOGGER.error("Patient id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            List<PatientAppointmentDetailsDTO> list = patientAppointmentService.getAppointmentDetailsByPatientId(patientId);
            return Response.ok(list).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get appointment details", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get appointment details");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    @Path("/outcome/{appointmentId}")
    public Response getOutcome(@PathParam("appointmentId") Integer appointmentId,
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

            if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to doctor role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (patientAppointmentValidator.isIdEmpty(appointmentId)) {
                LOGGER.error("Appointment id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientAppointmentValidator.isIdExists(appointmentId)) {
                LOGGER.error("Appointment id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            PatientAppointmentOutcomeDTO dto = patientAppointmentOutcomeService.getOutcomeByAppointmentId(appointmentId);
            if (dto == null) {
                LOGGER.error("Appointment outcome not found");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment outcome not found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(errorDTO)
                        .build();
            }
            return Response.ok(dto).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get appointment outcome", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get appointment outcome");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @PUT
    @Path("/outcome")
    public Response saveOrUpdateOutcome(PatientAppointmentOutcomeDTO dto,
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

            if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to doctor role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (outcomeValidator.isOutcomeIdEmpty(dto)) {
                LOGGER.error("Outcome id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Outcome id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (patientAppointmentValidator.isIdEmpty(dto.getAppointmentId())) {
                LOGGER.error("Appointment id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientAppointmentValidator.isIdExists(dto.getAppointmentId())) {
                LOGGER.error("Appointment id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!outcomeValidator.isResultValid(dto)) {
                LOGGER.error("Appointment outcome result is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment outcome result is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            PatientAppointmentOutcomeDTO appointmentOutcomeDTO = patientAppointmentOutcomeService.saveOrUpdateOutcome(dto);
            if (appointmentOutcomeDTO == null) {
                LOGGER.error("Appointment outcome was not updated");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Appointment outcome was not updated");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(errorDTO)
                        .build();
            }
            return Response.ok(appointmentOutcomeDTO).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to update appointment outcome", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to update appointment outcome");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

}
