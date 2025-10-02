package com.really.good.sir.resources;

import com.really.good.sir.converter.PatientAppointmentConverter;
import com.really.good.sir.dao.PatientAppointmentDAO;
import com.really.good.sir.dto.PatientAppointmentDTO;
import com.really.good.sir.entity.PatientAppointmentEntity;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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
}
