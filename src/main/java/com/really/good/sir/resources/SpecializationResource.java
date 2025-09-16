package com.really.good.sir.resources;

import com.really.good.sir.dao.SpecializationDAO;
import com.really.good.sir.models.Specialization;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/specializations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SpecializationResource {

    private final SpecializationDAO specializationDAO = new SpecializationDAO();

    @GET
    public Response getAllSpecializations() {
        final List<Specialization> specializations = specializationDAO.getAllSpecializations();
        return Response.ok(specializations).build();
    }

    @GET
    @Path("/{id}")
    public Response getSpecializationById(@PathParam("id") final int id) {
        final Specialization spec = specializationDAO.getSpecializationById(id);
        return Response.ok(spec).build();
    }
}
