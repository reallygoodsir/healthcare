package com.really.good.sir.resources;

import com.really.good.sir.converter.SpecializationConverter;
import com.really.good.sir.dao.SpecializationDAO;
import com.really.good.sir.dto.SpecializationDTO;
import com.really.good.sir.entity.SpecializationEntity;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/specializations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SpecializationResource {
    private final SpecializationConverter specializationConverter = new SpecializationConverter();
    private final SpecializationDAO specializationDAO = new SpecializationDAO();

    @GET
    public Response getAllSpecializations() {
        final List<SpecializationEntity> specializationEntities = specializationDAO.getAllSpecializations();
        final List<SpecializationDTO> specializationDTOs = specializationConverter.convert(specializationEntities);
        return Response.ok(specializationDTOs).build();
    }

    @GET
    @Path("/{specializationId}")
    public Response getSpecializationById(@PathParam("id") final int specializationId) {
        final SpecializationEntity specializationEntity = specializationDAO.getSpecializationById(specializationId);
        final SpecializationDTO specializationDTO = specializationConverter.convert(specializationEntity);
        return Response.ok(specializationDTO).build();
    }
}
