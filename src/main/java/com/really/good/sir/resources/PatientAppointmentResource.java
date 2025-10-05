package com.really.good.sir.resources;

import com.really.good.sir.converter.PatientAppointmentConverter;
import com.really.good.sir.dao.PatientAppointmentDAO;
import com.really.good.sir.dto.PatientAppointmentDTO;
import com.really.good.sir.entity.PatientAppointmentEntity;

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

    @GET
    public Response getAllAppointments() {
        List<PatientAppointmentEntity> list = dao.getAllAppointments();
        List<PatientAppointmentDTO> dtos = list.stream().map(converter::convert).toList();
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{doctorId}")
    public Response getAppointmentsByDoctor(@PathParam("doctorId") int doctorId) {
        List<PatientAppointmentEntity> list = dao.getAppointmentsByDoctorId(doctorId);
        List<PatientAppointmentDTO> dtos = list.stream().map(converter::convert).toList();
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/today/{doctorId}")
    public Response getTodaysAppointmentsByDoctor(@PathParam("doctorId") int doctorId) {
        List<PatientAppointmentEntity> list = dao.getTodaysAppointmentsByDoctor(doctorId);
        List<PatientAppointmentDTO> dtos = list.stream().map(converter::convert).toList();
        return Response.ok(dtos).build();
    }

    @POST
    public Response createAppointment(PatientAppointmentDTO dto) {
        PatientAppointmentEntity entity = converter.convert(dto);
        PatientAppointmentEntity created = dao.createAppointment(entity);
        return Response.ok(converter.convert(created)).build();
    }

    @DELETE
    @Path("/{appointmentId}")
    public Response deleteAppointment(@PathParam("appointmentId") int appointmentId) {
        boolean deleted = dao.deleteAppointment(appointmentId);
        return deleted ? Response.noContent().build() : Response.status(500).build();
    }

    @PATCH
    @Path("/{appointmentId}/status")
    public Response updateStatus(@PathParam("appointmentId") int appointmentId,
                                 @QueryParam("status") String status) {
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
    public Response getAppointmentStatus(@PathParam("appointmentId") int appointmentId) {
        String status = dao.getAppointmentStatusById(appointmentId);
        if (status == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Appointment not found")).build();
        }
        return Response.ok(Map.of("status", status)).build();
    }
}
