package com.really.good.sir.resources;

import com.really.good.sir.converter.DoctorConverter;
import com.really.good.sir.dao.DoctorDAO;
import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.entity.DoctorEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger LOGGER = LogManager.getLogger(DoctorResource.class);
    private final DoctorConverter doctorConverter = new DoctorConverter();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    @GET
    public Response getAllDoctors() {
        final List<DoctorEntity> doctorEntities = doctorDAO.getAllDoctors();
        final List<DoctorDTO> doctorDTOs = doctorConverter.convert(doctorEntities);
        return Response.ok(doctorDTOs).build();
    }

    @GET
    @Path("/{doctorId}")
    public Response getDoctor(@PathParam("doctorId") final int doctorId) {
        final DoctorEntity doctorEntity = doctorDAO.getDoctorById(doctorId);
        final DoctorDTO doctorDTO = doctorConverter.convert(doctorEntity);
        return Response.ok(doctorDTO).build();
    }

    @GET
    @Path("/service/{serviceId}")
    public Response getDoctorsByService(@PathParam("serviceId") final int serviceId) {
        final List<DoctorEntity> doctorEntities = doctorDAO.getDoctorsByServiceId(serviceId);
        final List<DoctorDTO> doctorDTOs = doctorConverter.convert(doctorEntities);
        return Response.ok(doctorDTOs).build();
    }

    @POST
    public Response createDoctor(final DoctorDTO doctorDTO, @Context final UriInfo uriInfo) {
        final DoctorEntity doctorEntity = doctorConverter.convert(doctorDTO);
        final DoctorEntity createdDoctorEntity = doctorDAO.createDoctor(doctorEntity);
        final DoctorDTO responseDoctorDTO = doctorConverter.convert(createdDoctorEntity);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(responseDoctorDTO.getId())).build())
                .entity(responseDoctorDTO).build();
    }

    @PUT
    public Response updateDoctor(final DoctorDTO requestDoctorDTO) {
        final DoctorEntity doctorEntity = doctorConverter.convert(requestDoctorDTO);
        final boolean isDoctorUpdated = doctorDAO.updateDoctor(doctorEntity);
        LOGGER.info("Doctor updated [{}]", isDoctorUpdated);
        final DoctorDTO responseDoctorDTO = doctorConverter.convert(doctorEntity);
        return Response.ok(responseDoctorDTO).build();
    }

    @DELETE
    @Path("/{doctorId}")
    public Response deleteDoctor(@PathParam("doctorId") final int doctorId) {
        final boolean isDoctorDeleted = doctorDAO.deleteDoctor(doctorId);
        LOGGER.info("Doctor deleted [{}]", isDoctorDeleted);
        return Response.noContent().build();
    }
}
