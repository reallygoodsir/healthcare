package com.really.good.sir.resources;

import com.really.good.sir.dao.DoctorDAO;
import com.really.good.sir.models.Doctor;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/doctors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DoctorResource {

    private final DoctorDAO doctorDAO = new DoctorDAO();

    @GET
    public Response getAllDoctors() {
        final List<Doctor> doctors = doctorDAO.getAllDoctors();
        return Response.ok(doctors).build();
    }

    @GET
    @Path("/{doctorId}")
    public Response getDoctor(@PathParam("doctorId") final int doctorId) {
        final Doctor doctor = doctorDAO.getDoctorById(doctorId);
        return Response.ok(doctor).build();
    }

    @POST
    public Response createDoctor(final Doctor doctor, @Context final UriInfo uriInfo) {
        final Doctor created = doctorDAO.createDoctor(doctor);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build())
                .entity(created).build();
    }

    @PUT
    public Response updateDoctor(final Doctor doctor) {
        doctorDAO.updateDoctor(doctor);
        return Response.ok(doctor).build();
    }

    @DELETE
    @Path("/{doctorId}")
    public Response deleteDoctor(@PathParam("doctorId") final int doctorId) {
        doctorDAO.deleteDoctor(doctorId);
        return Response.noContent().build();
    }
}

