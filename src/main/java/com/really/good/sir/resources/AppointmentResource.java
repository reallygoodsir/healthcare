package com.really.good.sir.resources;

import com.really.good.sir.dao.AppointmentDAO;
import com.really.good.sir.dao.AppointmentOutcomeDAO;
import com.really.good.sir.dto.AppointmentDTO;
import com.really.good.sir.dto.AppointmentOutcomeDTO;
import com.really.good.sir.entity.AppointmentEntity;
import com.really.good.sir.converter.AppointmentConverter;

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

    @POST
    public Response createAppointment(AppointmentDTO dto) {
        if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
            dto.setStatus("SCHEDULED");
        }
        AppointmentEntity entity = converter.convert(dto);
        AppointmentEntity created = appointmentDAO.createAppointment(entity);
        return Response.ok(converter.convert(created)).build();
    }

    @GET
    public Response getAllAppointments() {
        List<AppointmentEntity> appointments = appointmentDAO.getAllAppointments();
        return Response.ok(converter.convert(appointments)).build();
    }

    @GET
    @Path("/{appointmentId}")
    public Response getAppointmentById(@PathParam("appointmentId") int appointmentId) {
        AppointmentEntity entity = appointmentDAO.getAppointmentById(appointmentId);
        if (entity != null) {
            return Response.ok(converter.convert(entity)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // --- New endpoint to get outcome separately ---
    @GET
    @Path("/{appointmentId}/outcome")
    public Response getAppointmentOutcome(@PathParam("appointmentId") int appointmentId) {
        AppointmentOutcomeDTO outcome = outcomeDAO.getOutcomeByAppointmentId(appointmentId);
        if (outcome != null) {
            return Response.ok(outcome).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{appointmentId}")
    public Response updateAppointmentOutcome(@PathParam("appointmentId") int appointmentId, AppointmentOutcomeDTO dto) {
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
    public Response updateStatus(@PathParam("appointmentId") int appointmentId, @QueryParam("status") String status) {
        boolean success = appointmentDAO.updateAppointmentStatus(appointmentId, status);
        if (success) return Response.noContent().build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{appointmentId}")
    public Response deleteAppointment(@PathParam("appointmentId") int appointmentId) {
        boolean deleted = appointmentDAO.deleteAppointment(appointmentId);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
