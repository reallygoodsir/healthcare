package com.really.good.sir.resources;

import com.really.good.sir.dto.DoctorScheduleDTO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.UserSessionDTO;
import com.really.good.sir.entity.Role;
import com.really.good.sir.service.DoctorScheduleService;
import com.really.good.sir.service.UserSessionService;
import com.really.good.sir.validator.DoctorScheduleValidator;
import com.really.good.sir.validator.DoctorValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/doctor-schedules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DoctorScheduleResource {
    private static final Logger LOGGER = LogManager.getLogger(DoctorScheduleResource.class);
    private final DoctorScheduleService scheduleService = new DoctorScheduleService();
    private final UserSessionService userSessionService = new UserSessionService();
    private final DoctorScheduleValidator doctorScheduleValidator = new DoctorScheduleValidator();
    private final DoctorValidator doctorValidator = new DoctorValidator();

    @GET
    @Path("/{doctorId}")
    public Response getSchedulesByDoctor(@PathParam("doctorId") final Integer doctorId,
                                         @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
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
            final List<DoctorScheduleDTO> schedules = scheduleService.getSchedulesByDoctor(doctorId);
            return Response.ok(schedules).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get schedules by doctor id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get schedules by doctor id");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    @Path("/appointments/{doctorId}")
    public Response getSchedulesWithAppointments(@PathParam("doctorId") final int doctorId,
                                                 @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
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

            final List<DoctorScheduleDTO> schedules = scheduleService.getSchedulesForTodayWithAppointments(doctorId);
            return Response.ok(schedules).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get schedules with appointments by doctor id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get schedules with appointments by doctor id");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @POST
    public Response createSchedule(final DoctorScheduleDTO requestScheduleDTO,
                                   @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
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

            if (requestScheduleDTO.getId() != null) {
                LOGGER.error("Doctor schedule id must be empty when new schedule is created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor schedule id must be empty when new schedule is created");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (doctorValidator.isIdEmpty(requestScheduleDTO.getDoctorId())) {
                LOGGER.error("Doctor id must not be empty when new schedule is created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id must not be empty when new schedule is created");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.idExists(requestScheduleDTO.getDoctorId())) {
                LOGGER.error("Doctor id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorScheduleValidator.isScheduleDateValid(requestScheduleDTO)) {
                LOGGER.error("Date must be not be in the past");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Date must be not be in the past");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorScheduleValidator.isTimeRangeValid(requestScheduleDTO)) {
                LOGGER.error("Invalid start/end time");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Invalid start/end time");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (doctorScheduleValidator.isOverlapping(requestScheduleDTO)) {
                LOGGER.error("Time overlaps with an existing schedule");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Time overlaps with an existing schedule");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            final DoctorScheduleDTO createdEntity = scheduleService.createSchedule(requestScheduleDTO);
            if (createdEntity == null) {
                LOGGER.error("Doctor schedule is not created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor schedule is not created");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.ok(createdEntity).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to create doctor schedule", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to create doctor schedule");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @PUT
    public Response updateSchedule(final DoctorScheduleDTO requestScheduleDTO,
                                   @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
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


            if (doctorScheduleValidator.isScheduleIdEmpty(requestScheduleDTO)) {
                LOGGER.error("Doctor schedule id must not be empty when existing schedule is updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor schedule id must not be empty when existing schedule is updated");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorScheduleValidator.isScheduleDateValid(requestScheduleDTO)) {
                LOGGER.error("Date must be not be in the past");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Date must be not be in the past");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorScheduleValidator.isScheduleIdExists(requestScheduleDTO)) {
                LOGGER.error("Schedule id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Schedule id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (doctorValidator.isIdEmpty(requestScheduleDTO.getDoctorId())) {
                LOGGER.error("Doctor id must not be empty when existing schedule is updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id must not be empty when existing doctor is updated");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.idExists(requestScheduleDTO.getDoctorId())) {
                LOGGER.error("Doctor id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorScheduleValidator.isTimeRangeValid(requestScheduleDTO)) {
                LOGGER.error("Invalid start/end time");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Invalid start/end time");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (doctorScheduleValidator.isOverlapping(requestScheduleDTO)) {
                LOGGER.error("Time overlaps with an existing schedule");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Time overlaps with an existing schedule");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            DoctorScheduleDTO doctorScheduleDTO = scheduleService.updateSchedule(requestScheduleDTO);
            if (doctorScheduleDTO == null) {
                LOGGER.error("Doctor schedule is not updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor schedule is not updated");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.ok(doctorScheduleDTO).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to update doctor schedule", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to update doctor schedule");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @DELETE
    @Path("/{doctorId}/{scheduleId}")
    public Response deleteSchedule(@PathParam("doctorId") final Integer doctorId,
                                   @PathParam("scheduleId") final Integer scheduleId,
                                   @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
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

            if (doctorScheduleValidator.isScheduleIdEmpty(scheduleId)) {
                LOGGER.error("Schedule id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Schedule id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorScheduleValidator.isScheduleIdExists(scheduleId)) {
                LOGGER.error("Schedule id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Schedule id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.idExists(doctorId)) {
                LOGGER.error("Doctor id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (doctorValidator.isIdEmpty(doctorId)) {
                LOGGER.error("Doctor id must not be empty when existing doctor schedule is deleted");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id must not be empty when existing doctor schedule is deleted");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            final boolean isScheduleEntityDeleted = scheduleService.deleteSchedule(scheduleId);
            if (!isScheduleEntityDeleted) {
                LOGGER.error("Doctor schedule is not deleted");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor schedule is not deleted");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.noContent().build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to delete doctor schedule", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to delete doctor schedule");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }
}
