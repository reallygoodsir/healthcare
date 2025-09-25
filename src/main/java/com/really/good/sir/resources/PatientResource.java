package com.really.good.sir.resources;

import com.really.good.sir.converter.PatientConverter;
import com.really.good.sir.dao.PatientDAO;
import com.really.good.sir.dto.PatientDTO;
import com.really.good.sir.entity.PatientEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/patients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientResource {
    private static final Logger LOGGER = LogManager.getLogger(PatientResource.class);
    private final PatientConverter patientConverter = new PatientConverter();
    private final PatientDAO patientDAO = new PatientDAO();

    @GET
    public Response getAllPatients() {
        final List<PatientEntity> patientEntities = patientDAO.getAllPatients();
        final List<PatientDTO> patientDTOs = patientConverter.convert(patientEntities);
        return Response.ok(patientDTOs).build();
    }

    @GET
    @Path("/{patientId}")
    public Response getPatientById(@PathParam("patientId") final int patientId) {
        final PatientEntity patientEntity = patientDAO.getPatientById(patientId);
        final PatientDTO patientDTO = patientConverter.convert(patientEntity);
        return Response.ok(patientDTO).build();
    }

    @GET
    @Path("/visits/{phoneNumber}")
    public Response getPatientByPhone(@PathParam("phoneNumber") final String phoneNumber) {
        LOGGER.info("0");
        final PatientEntity patientEntity = patientDAO.getPatientByPhone(phoneNumber);
        LOGGER.info("1 {}", patientEntity);
        final PatientDTO patientDTO = patientConverter.convert(patientEntity);
        LOGGER.info("2");
        return Response.ok(patientDTO).build();
    }

    @POST
    public Response createPatient(final PatientDTO requestPatientDTO) {
        final PatientEntity patientEntity = patientConverter.convert(requestPatientDTO);
        final PatientEntity createdEntity = patientDAO.createPatient(patientEntity);
        final PatientDTO responsePatientDTO = patientConverter.convert(createdEntity);
        return Response.ok(responsePatientDTO).build();
    }

    @PUT
    public Response updatePatient(final PatientDTO requestPatientDTO) {
        final PatientEntity patientEntity = patientConverter.convert(requestPatientDTO);
        final boolean isPatientUpdated = patientDAO.updatePatient(patientEntity);
        LOGGER.info("Is patient updated [{}]", isPatientUpdated);
        final PatientDTO responsePatientDTO = patientConverter.convert(patientEntity);
        return Response.ok(responsePatientDTO).build();
    }

    @DELETE
    @Path("/{patientId}")
    public Response deletePatient(@PathParam("patientId") final int patientId) {
        final boolean isPatientDeleted = patientDAO.deletePatient(patientId);
        LOGGER.info("Is patient deleted [{}]", isPatientDeleted);
        return Response.noContent().build();
    }
}
