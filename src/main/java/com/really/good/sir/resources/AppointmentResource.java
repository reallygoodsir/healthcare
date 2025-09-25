package com.really.good.sir.resources;

import com.really.good.sir.converter.AppointmentConverter;
import com.really.good.sir.dao.AppointmentDAO;
import com.really.good.sir.dto.AppointmentDTO;
import com.really.good.sir.entity.AppointmentEntity;
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
    private final AppointmentConverter converter = new AppointmentConverter();

    @POST
    public Response createAppointment(AppointmentDTO dto) {
        dto.setStatus("SCHEDULED"); // always scheduled
        AppointmentEntity entity = converter.convert(dto);
        AppointmentEntity created = appointmentDAO.createAppointment(entity);
        return Response.ok(converter.convert(created)).build();
    }

    @GET
    @Path("/{doctorId}/{scheduleId}")
    public Response getAppointmentByIds(@PathParam("doctorId") int doctorId,
                                        @PathParam("scheduleId") int scheduleId) {
        List<AppointmentEntity> appointments = appointmentDAO.getAppointmentByIds(doctorId, scheduleId);
        return Response.ok(converter.convert(appointments)).build();
    }

    @GET
    @Path("/doctor/{doctorId}")
    public Response getAppointmentsByDoctor(@PathParam("doctorId") int doctorId) {
        List<AppointmentEntity> appointments = appointmentDAO.getAppointmentsByDoctor(doctorId);
        return Response.ok(converter.convert(appointments)).build();
    }

    @DELETE
    @Path("/{appointmentId}")
    public Response deleteAppointment(@PathParam("appointmentId") int appointmentId) {
        boolean deleted = appointmentDAO.deleteAppointment(appointmentId);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
