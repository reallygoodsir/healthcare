package com.really.good.sir.resources;

import com.really.good.sir.converter.DoctorScheduleConverter;
import com.really.good.sir.dao.DoctorScheduleDAO;
import com.really.good.sir.dto.DoctorScheduleDTO;
import com.really.good.sir.entity.DoctorScheduleEntity;
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

    @GET
    @Path("/{doctorId}")
    public Response getSchedulesByDoctor(@PathParam("doctorId") final int doctorId) {
        final List<DoctorScheduleEntity> schedules = scheduleDAO.getSchedulesByDoctor(doctorId);
        final List<DoctorScheduleDTO> scheduleDTOs = scheduleConverter.convert(schedules);
        return Response.ok(scheduleDTOs).build();
    }

    @POST
    public Response createSchedule(final DoctorScheduleDTO requestScheduleDTO) {
        final DoctorScheduleEntity scheduleEntity = scheduleConverter.convert(requestScheduleDTO);
        final DoctorScheduleEntity createdEntity = scheduleDAO.createSchedule(scheduleEntity);
        final DoctorScheduleDTO responseScheduleDTO = scheduleConverter.convert(createdEntity);
        return Response.ok(responseScheduleDTO).build();
    }

    @PUT
    public Response updateSchedule(final DoctorScheduleDTO requestScheduleDTO) {
        final DoctorScheduleEntity scheduleEntity = scheduleConverter.convert(requestScheduleDTO);
        final boolean isScheduleEntityUpdated = scheduleDAO.updateSchedule(scheduleEntity);
        LOGGER.info("Schedule updated [{}]", isScheduleEntityUpdated);
        final DoctorScheduleDTO responseScheduleDTO = scheduleConverter.convert(scheduleEntity);
        return Response.ok(responseScheduleDTO).build();
    }

    @DELETE
    @Path("/{doctorId}/{scheduleId}")
    public Response deleteSchedule(@PathParam("doctorId") final int doctorId,
                                   @PathParam("scheduleId") final int scheduleId) {
        final boolean isScheduleEntityDeleted = scheduleDAO.deleteSchedule(scheduleId);
        LOGGER.info("Schedule deleted [{}]", isScheduleEntityDeleted);
        return Response.noContent().build();
    }
}

