package com.really.good.sir.resources;

import com.really.good.sir.converter.PatientConverter;
import com.really.good.sir.dao.PatientDAO;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.PatientDTO;
import com.really.good.sir.entity.PatientEntity;
import com.really.good.sir.entity.Role;
import com.really.good.sir.entity.UserSessionEntity;
import com.really.good.sir.validator.PatientValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/patients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientResource {
    private static final Logger LOGGER = LogManager.getLogger(PatientResource.class);
    private final PatientConverter patientConverter = new PatientConverter();
    private final PatientValidator patientValidator = new PatientValidator();
    private final PatientDAO patientDAO = new PatientDAO();
    private final UserSessionDAO userSessionDAO = new UserSessionDAO();


    // CUD = admin; R - {phone=cca, id=cca+doctor, all=admin}
    @GET
    public Response getAllPatients(@CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        final List<PatientEntity> patientEntities = patientDAO.getAllPatients();
        final List<PatientDTO> patientDTOs = patientConverter.convert(patientEntities);
        return Response.ok(patientDTOs).build();
    }

    @GET
    @Path("/by-credential/{credentialId}")
    public Response getPatientIdByCredential(@PathParam("credentialId") final int credentialId,
                                             @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }

        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.PATIENT.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }

        final int patientId = patientDAO.getPatientIdByCredentialId(credentialId);
        if (patientId == -1) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Patient not found for credential ID: " + credentialId)
                    .build();
        }

        return Response.ok(patientId).build();
    }


    @GET
    @Path("/{patientId}")
    public Response getPatientById(@PathParam("patientId") final int patientId, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole()) && !Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        final PatientEntity patientEntity = patientDAO.getPatientById(patientId);
        final PatientDTO patientDTO = patientConverter.convert(patientEntity);
        return Response.ok(patientDTO).build();
    }

    @GET
    @Path("/visits/{phoneNumber}")
    public Response getPatientByPhone(@PathParam("phoneNumber") final String phoneNumber, @CookieParam("session_id") final String sessionId) {
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
        final PatientEntity patientEntity = patientDAO.getPatientByPhone(phoneNumber);
        final PatientDTO patientDTO = patientConverter.convert(patientEntity);
        return Response.ok(patientDTO).build();
    }

    @POST
    public Response createPatient(final PatientDTO requestPatientDTO, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isFirstNameValid(requestPatientDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("First name has the wrong format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isLastNameValid(requestPatientDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Last name has the wrong format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isAddressValid(requestPatientDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("No Address provided");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isDateOfBirthValid(requestPatientDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Unfitting date of birth");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isEmailValid(requestPatientDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Email is either of the wrong format or already exists");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isPhoneValid(requestPatientDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Phone number is either of the wrong format or already exists");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        final PatientEntity patientEntity = patientConverter.convert(requestPatientDTO);
        final PatientEntity createdEntity = patientDAO.createPatient(patientEntity);
        final PatientDTO responsePatientDTO = patientConverter.convert(createdEntity);
        final URI location = URI.create("/healthcare/api/patients/" + createdEntity.getId());
        return Response.created(location)
                .entity(responsePatientDTO)
                .build();
    }

    @PUT
    public Response updatePatient(final PatientDTO requestPatientDTO, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isFirstNameValid(requestPatientDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("First name has the wrong format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isLastNameValid(requestPatientDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Last name has the wrong format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isAddressValid(requestPatientDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("No Address provided");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isDateOfBirthValid(requestPatientDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Unfitting date of birth");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isEmailValid(requestPatientDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Email is either of the wrong format or already exists");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }

        if (!patientValidator.isPhoneValid(requestPatientDTO)) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Phone number is either of the wrong format or already exists");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO)
                    .build();
        }
        final PatientEntity patientEntity = patientConverter.convert(requestPatientDTO);
        patientDAO.updatePatient(patientEntity);
        final PatientDTO responsePatientDTO = patientConverter.convert(patientEntity);
        return Response.ok(responsePatientDTO).build();
    }

    @DELETE
    @Path("/{patientId}")
    public Response deletePatient(@PathParam("patientId") final int patientId, @CookieParam("session_id") final String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Not authorized");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorDTO)
                    .build();
        }
        UserSessionEntity session = userSessionDAO.getSessionById(Integer.parseInt(sessionId));
        if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Forbidden to access resource");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDTO)
                    .build();
        }
        final boolean isPatientDeleted = patientDAO.deletePatient(patientId);
        LOGGER.info("Is patient deleted [{}]", isPatientDeleted);
        return Response.noContent().build();
    }
}
