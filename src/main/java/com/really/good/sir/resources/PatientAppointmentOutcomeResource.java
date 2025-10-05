package com.really.good.sir.resources;

import com.really.good.sir.dao.PatientAppointmentOutcomeDAO;
import com.really.good.sir.dto.PatientAppointmentOutcomeDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/patient-appointments/outcome")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientAppointmentOutcomeResource {

    private final PatientAppointmentOutcomeDAO dao = new PatientAppointmentOutcomeDAO();

    @GET
    @Path("/{appointmentId}")
    public Response getOutcome(@PathParam("appointmentId") int appointmentId) {
        PatientAppointmentOutcomeDTO dto = dao.getOutcomeByAppointmentId(appointmentId);
        if (dto != null) {
            return Response.ok(dto).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{appointmentId}")
    public Response saveOrUpdateOutcome(@PathParam("appointmentId") int appointmentId, PatientAppointmentOutcomeDTO dto) {
        try {
            dto.setAppointmentId(appointmentId);
            PatientAppointmentOutcomeDTO saved = dao.saveOrUpdateOutcome(dto);
            return Response.ok(saved).build();
        } catch (IllegalStateException ise) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ise.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to save or update outcome")
                    .build();
        }
    }
}
