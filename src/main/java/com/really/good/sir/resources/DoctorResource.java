package com.really.good.sir.resources;

import com.really.good.sir.converter.DoctorConverter;
import com.really.good.sir.dao.DoctorDAO;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.dto.DoctorIdDTO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.entity.DoctorEntity;
import com.really.good.sir.entity.Role;
import com.really.good.sir.entity.UserSessionEntity;
import com.really.good.sir.validator.DoctorValidator;
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
    private final UserSessionDAO userSessionDAO = new UserSessionDAO();
    private final DoctorValidator doctorValidator = new DoctorValidator();

    @GET
    public Response getAllDoctors(@CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole()) && !Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        final List<DoctorEntity> doctorEntities = doctorDAO.getAllDoctors();
        final List<DoctorDTO> doctorDTOs = doctorConverter.convert(doctorEntities);
        return Response.ok(doctorDTOs).build();
    }

    @GET
    @Path("/{doctorId}")
    public Response getDoctor(@PathParam("doctorId") final int doctorId, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        final DoctorEntity doctorEntity = doctorDAO.getDoctorById(doctorId);
        final DoctorDTO doctorDTO = doctorConverter.convert(doctorEntity);
        return Response.ok(doctorDTO).build();
    }

    @GET
    @Path("/service/{serviceId}")
    public Response getDoctorsByService(@PathParam("serviceId") final int serviceId, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        final List<DoctorEntity> doctorEntities = doctorDAO.getDoctorsByServiceId(serviceId);
        final List<DoctorDTO> doctorDTOs = doctorConverter.convert(doctorEntities);
        return Response.ok(doctorDTOs).build();
    }

    @GET
    @Path("/by-credential/{credentialId}")
    public Response getDoctorIdByCredential(@PathParam("credentialId") final int credentialId, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        final int doctorId = doctorDAO.getDoctorIdByCredentialId(credentialId);
        if (doctorId == -1) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not found");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorDTO)
                    .build();
        }
        DoctorIdDTO doctorIdDTO = new DoctorIdDTO();
        doctorIdDTO.setId(doctorId);
        return Response.ok(doctorIdDTO).build();
    }

    @POST
    public Response createDoctor(final DoctorDTO doctorDTO, @Context final UriInfo uriInfo, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }

        int sessionIdInt;
        try {
            sessionIdInt = Integer.parseInt(sessionId);
        } catch (NumberFormatException e) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }

        UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
        if (session == null || !Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        if (!doctorValidator.isFirstNameValid(doctorDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("First name has the wrong format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!doctorValidator.isLastNameValid(doctorDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Last name has the wrong format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!doctorValidator.isSpecializationIdValid(doctorDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("No Specialization provided");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!doctorValidator.isEmailValid(doctorDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Email is either of the wrong format or already exists");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!doctorValidator.isPhoneValid(doctorDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Phone number is either of the wrong format or already exists");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        final DoctorEntity doctorEntity = doctorConverter.convert(doctorDTO);
        final DoctorEntity createdDoctorEntity = doctorDAO.createDoctor(doctorEntity);
        final DoctorDTO responseDoctorDTO = doctorConverter.convert(createdDoctorEntity);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(responseDoctorDTO.getId())).build())
                .entity(responseDoctorDTO).build();
    }

    @PUT
    public Response updateDoctor(final DoctorDTO doctorDTO, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }

        int sessionIdInt;
        try {
            sessionIdInt = Integer.parseInt(sessionId);
        } catch (NumberFormatException e) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }

        UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
        if (session == null || !Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        if (!doctorValidator.isFirstNameValid(doctorDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("First name has the wrong format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!doctorValidator.isLastNameValid(doctorDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Last name has the wrong format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!doctorValidator.isSpecializationIdValid(doctorDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("No Specialization provided");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!doctorValidator.isEmailValid(doctorDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Email is either of the wrong format or already exists");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!doctorValidator.isPhoneValid(doctorDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Phone number is either of the wrong format or already exists");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        final DoctorEntity doctorEntity = doctorConverter.convert(doctorDTO);
        final boolean isDoctorUpdated = doctorDAO.updateDoctor(doctorEntity);
        LOGGER.info("Doctor updated [{}]", isDoctorUpdated);
        final DoctorDTO responseDoctorDTO = doctorConverter.convert(doctorEntity);
        return Response.ok(responseDoctorDTO).build();
    }

    @DELETE
    @Path("/{doctorId}")
    public Response deleteDoctor(@PathParam("doctorId") final int doctorId, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }

        int sessionIdInt;
        try {
            sessionIdInt = Integer.parseInt(sessionId);
        } catch (NumberFormatException e) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }

        UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
        if (session == null || !Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        final boolean isDoctorDeleted = doctorDAO.deleteDoctor(doctorId);
        if(isDoctorDeleted) {
            LOGGER.info("Doctor deleted [{}]", isDoctorDeleted);
            return Response.noContent().build();
        }
        final ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage("Incorrect/absent id");
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorDTO)
                .build();
    }
}
