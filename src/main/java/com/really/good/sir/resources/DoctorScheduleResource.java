package com.really.good.sir.resources;

import com.really.good.sir.converter.DoctorScheduleConverter;
import com.really.good.sir.dao.DoctorScheduleDAO;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.DoctorScheduleDTO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.entity.DoctorScheduleEntity;
import com.really.good.sir.entity.Role;
import com.really.good.sir.entity.UserSessionEntity;
import com.really.good.sir.validator.DoctorScheduleValidator;
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
    private final DoctorScheduleConverter scheduleConverter = new DoctorScheduleConverter();
    private final DoctorScheduleDAO scheduleDAO = new DoctorScheduleDAO();
    private final UserSessionDAO userSessionDAO = new UserSessionDAO();
    private final DoctorScheduleValidator doctorScheduleValidator = new DoctorScheduleValidator();

    @GET
    @Path("/{doctorId}")
    public Response getSchedulesByDoctor(@PathParam("doctorId") final int doctorId, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole()) && !Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        final List<DoctorScheduleEntity> schedules = scheduleDAO.getSchedulesByDoctor(doctorId);
        final List<DoctorScheduleDTO> scheduleDTOs = scheduleConverter.convert(schedules);
        return Response.ok(scheduleDTOs).build();
    }

    @GET
    @Path("/today/{doctorId}")
    public Response getSchedulesForTodayWithAppointments(@PathParam("doctorId") final int doctorId, @CookieParam("session_id") final String sessionId) {
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
        final List<DoctorScheduleEntity> schedules = scheduleDAO.getSchedulesForTodayWithAppointments(doctorId);
        final List<DoctorScheduleDTO> scheduleDTOs = scheduleConverter.convert(schedules);
        return Response.ok(scheduleDTOs).build();
    }

    @POST
    public Response createSchedule(final DoctorScheduleDTO requestScheduleDTO, @CookieParam("session_id") final String sessionId) {
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

        if (!doctorScheduleValidator.isScheduleDateValid(requestScheduleDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Date must be not be in the past");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!doctorScheduleValidator.isTimeRangeValid(requestScheduleDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Invalid start/end time");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (doctorScheduleValidator.isOverlapping(requestScheduleDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Time overlaps with an existing schedule");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        final DoctorScheduleEntity scheduleEntity = scheduleConverter.convert(requestScheduleDTO);
        final DoctorScheduleEntity createdEntity = scheduleDAO.createSchedule(scheduleEntity);
        final DoctorScheduleDTO responseScheduleDTO = scheduleConverter.convert(createdEntity);
        return Response.ok(responseScheduleDTO).build();
    }

    @PUT
    public Response updateSchedule(final DoctorScheduleDTO requestScheduleDTO, @CookieParam("session_id") final String sessionId) {
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

        if (!doctorScheduleValidator.isScheduleDateValid(requestScheduleDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Date must be not be in the past");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!doctorScheduleValidator.isTimeRangeValid(requestScheduleDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Invalid start/end time");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (doctorScheduleValidator.isOverlapping(requestScheduleDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Time overlaps with an existing schedule");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        final DoctorScheduleEntity scheduleEntity = scheduleConverter.convert(requestScheduleDTO);
        final boolean isScheduleEntityUpdated = scheduleDAO.updateSchedule(scheduleEntity);
        LOGGER.info("Schedule updated [{}]", isScheduleEntityUpdated);
        final DoctorScheduleDTO responseScheduleDTO = scheduleConverter.convert(scheduleEntity);
        if(isScheduleEntityUpdated) return Response.ok(responseScheduleDTO).build();

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage("Failed to update schedule");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorDTO)
                .build();
    }

    @DELETE
    @Path("/{doctorId}/{scheduleId}")
    public Response deleteSchedule(@PathParam("doctorId") final int doctorId,
                                   @PathParam("scheduleId") final int scheduleId,
                                   @CookieParam("session_id") final String sessionId) {
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

        final boolean isScheduleEntityDeleted = scheduleDAO.deleteSchedule(scheduleId);
        LOGGER.info("Schedule deleted [{}]", isScheduleEntityDeleted);
        if(isScheduleEntityDeleted) return Response.noContent().build();

        final ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage("Incorrect/absent id");
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorDTO)
                .build();
    }
}
