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

    @GET
    public Response getAllPatients(@CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is empty");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            int sessionIdInt;
            try {
                sessionIdInt = Integer.parseInt(sessionId);
            } catch (NumberFormatException exception) {
                LOGGER.error("Session id is not valid", exception);
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id has incorrect format");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
            if (session == null) {
                LOGGER.error("Session id does not exist [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to admin role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            final List<PatientEntity> patientEntities = patientDAO.getAllPatients();
            final List<PatientDTO> patientDTOs = patientConverter.convert(patientEntities);
            return Response.ok(patientDTOs).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get all patients", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get all patients");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    @Path("/credentials/{credentialId}")
    public Response getPatientIdByCredential(@PathParam("credentialId") final Integer credentialId,
                                             @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is empty");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            int sessionIdInt;
            try {
                sessionIdInt = Integer.parseInt(sessionId);
            } catch (NumberFormatException exception) {
                LOGGER.error("Session id is not valid", exception);
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id has incorrect format");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
            if (session == null) {
                LOGGER.error("Session id does not exist [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.PATIENT.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to patient role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (patientValidator.isCredentialIdEmpty(credentialId)) {
                LOGGER.error("Credential id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Credential id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.credentialIdExists(credentialId)) {
                LOGGER.error("Credential id does not exist");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Credential id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            final int patientId = patientDAO.getPatientIdByCredentialId(credentialId);
            if (patientId == -1) {
                LOGGER.error("Patient id was not found");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id was not found");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.ok(patientId).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get patient id by credential id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get patient id by credential id");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }


    @GET
    @Path("/{patientId}")
    public Response getPatientById(@PathParam("patientId") final Integer patientId,
                                   @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is empty");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            int sessionIdInt;
            try {
                sessionIdInt = Integer.parseInt(sessionId);
            } catch (NumberFormatException exception) {
                LOGGER.error("Session id is not valid", exception);
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id has incorrect format");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
            if (session == null) {
                LOGGER.error("Session id does not exist [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole()) &&
                    !Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to doctor or call center agent role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (patientValidator.isPatientIdEmpty(patientId)) {
                LOGGER.error("Patient id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isPatientIdExists(patientId)) {
                LOGGER.error("Patient id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            final PatientEntity patientEntity = patientDAO.getPatientById(patientId);
            if (patientEntity == null) {
                LOGGER.error("Patient was not found");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient was not found");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            final PatientDTO patientDTO = patientConverter.convert(patientEntity);
            return Response.ok(patientDTO).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get patient by id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get patient by id");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    @Path("/visits/{phoneNumber}")
    public Response getPatientByPhone(@PathParam("phoneNumber") final String phoneNumber,
                                      @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is empty");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            int sessionIdInt;
            try {
                sessionIdInt = Integer.parseInt(sessionId);
            } catch (NumberFormatException exception) {
                LOGGER.error("Session id is not valid", exception);
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id has incorrect format");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
            if (session == null) {
                LOGGER.error("Session id does not exist [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to call center agent role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (patientValidator.isPhoneEmpty(phoneNumber)) {
                LOGGER.error("Phone number is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isPhoneExists(phoneNumber)) {
                LOGGER.error("Phone number does not exist");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            final PatientEntity patientEntity = patientDAO.getPatientByPhone(phoneNumber);
            if (patientEntity == null) {
                LOGGER.error("Patient was not found");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient was not found");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            final PatientDTO patientDTO = patientConverter.convert(patientEntity);
            return Response.ok(patientDTO).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get patient by phone number", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get patient by phone number");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @POST
    public Response createPatient(final PatientDTO requestPatientDTO,
                                  @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is empty");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            int sessionIdInt;
            try {
                sessionIdInt = Integer.parseInt(sessionId);
            } catch (NumberFormatException exception) {
                LOGGER.error("Session id is not valid", exception);
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id has incorrect format");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
            if (session == null) {
                LOGGER.error("Session id does not exist [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to admin role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isPatientIdEmpty(requestPatientDTO)) {
                LOGGER.error("Patient id must be empty when new patient is created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id must be empty when new patient is created");
                return Response.status(Response.Status.BAD_REQUEST)
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

            if (!patientValidator.isEmailValid(requestPatientDTO)) {
                LOGGER.error("Email has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isEmailUnique(requestPatientDTO)) {
                LOGGER.error("Email already exists");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email already exists");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isPhoneValid(requestPatientDTO)) {
                LOGGER.error("Phone number has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isPhoneUnique(requestPatientDTO)) {
                LOGGER.error("Phone number already exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number already exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isAddressValid(requestPatientDTO)) {
                LOGGER.error("No Address provided");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No Address provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isDateOfBirthValid(requestPatientDTO)) {
                LOGGER.error("Unfitting date of birth");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Unfitting date of birth");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            final PatientEntity patientEntity = patientConverter.convert(requestPatientDTO);
            final PatientEntity createdEntity = patientDAO.createPatient(patientEntity);
            if (createdEntity == null) {
                LOGGER.error("Patient is not created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient is not created");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            final PatientDTO responsePatientDTO = patientConverter.convert(createdEntity);
            final URI location = URI.create("/healthcare/api/patients/" + createdEntity.getId());
            return Response.created(location)
                    .entity(responsePatientDTO)
                    .build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to create patient", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to create patient");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @PUT
    public Response updatePatient(final PatientDTO requestPatientDTO,
                                  @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is empty");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            int sessionIdInt;
            try {
                sessionIdInt = Integer.parseInt(sessionId);
            } catch (NumberFormatException exception) {
                LOGGER.error("Session id is not valid", exception);
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id has incorrect format");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
            if (session == null) {
                LOGGER.error("Session id does not exist [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to admin role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (patientValidator.isPatientIdEmpty(requestPatientDTO)) {
                LOGGER.error("Patient id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isPatientIdExists(requestPatientDTO)) {
                LOGGER.error("Patient id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
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

            if (!patientValidator.isEmailValid(requestPatientDTO)) {
                LOGGER.error("Email has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isEmailUnique(requestPatientDTO)) {
                LOGGER.error("Email already exists");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email already exists");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isPhoneValid(requestPatientDTO)) {
                LOGGER.error("Phone number has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isPhoneUnique(requestPatientDTO)) {
                LOGGER.error("Phone number already exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number already exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isAddressValid(requestPatientDTO)) {
                LOGGER.error("No Address provided");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No Address provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isDateOfBirthValid(requestPatientDTO)) {
                LOGGER.error("Unfitting date of birth");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Unfitting date of birth");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            final PatientEntity patientEntity = patientConverter.convert(requestPatientDTO);
            boolean updated = patientDAO.updatePatient(patientEntity);
            if (!updated) {
                LOGGER.error("Patient is not updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient is not updated");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            final PatientDTO responsePatientDTO = patientConverter.convert(patientEntity);
            return Response.ok()
                    .entity(responsePatientDTO)
                    .build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to update patient", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to update patient");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @DELETE
    @Path("/{patientId}")
    public Response deletePatient(@PathParam("patientId") final Integer patientId,
                                  @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                LOGGER.error("Session id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is empty");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            int sessionIdInt;
            try {
                sessionIdInt = Integer.parseInt(sessionId);
            } catch (NumberFormatException exception) {
                LOGGER.error("Session id is not valid", exception);
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id has incorrect format");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            UserSessionEntity session = userSessionDAO.getSessionById(sessionIdInt);
            if (session == null) {
                LOGGER.error("Session id does not exist [{}]", sessionId);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Not authorized. Session id does not exist");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(errorDTO)
                        .build();
            }

            if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to admin role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (patientValidator.isPatientIdEmpty(patientId)) {
                LOGGER.error("Patient id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!patientValidator.isPatientIdExists(patientId)) {
                LOGGER.error("Patient id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            final boolean isPatientDeleted = patientDAO.deletePatient(patientId);
            if (!isPatientDeleted) {
                LOGGER.error("Patient is not deleted");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient is not deleted");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.noContent().build();

        } catch (final Exception exception) {
            LOGGER.error("Error trying to delete patient", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to delete patient");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }
}
