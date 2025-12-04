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
import com.really.good.sir.validator.ServiceValidator;
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

    private final ServiceValidator serviceValidator = new ServiceValidator();

    @GET
    public Response getAllDoctors(@CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
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

            if (!Role.ADMIN.toString().equalsIgnoreCase(session.getRole()) &&
                    !Role.CALL_CENTER_AGENT.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to admin or call center agent role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }
            final List<DoctorEntity> doctorEntities = doctorDAO.getAllDoctors();
            final List<DoctorDTO> doctorDTOs = doctorConverter.convert(doctorEntities);
            return Response.ok(doctorDTOs).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get all doctors", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get all doctors");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    @Path("/{doctorId}")
    public Response getDoctor(@PathParam("doctorId") final Integer doctorId,
                              @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
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

            if (doctorValidator.isIdEmpty(doctorId)) {
                LOGGER.error("Doctor id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No doctor id provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.idExists(doctorId)) {
                LOGGER.error("Doctor id does not exist");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            final DoctorEntity doctorEntity = doctorDAO.getDoctorById(doctorId);
            final DoctorDTO doctorDTO = doctorConverter.convert(doctorEntity);
            return Response.ok(doctorDTO).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get doctor by id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get doctor by id");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    @Path("/service/{serviceId}")
    public Response getDoctorsByService(@PathParam("serviceId") final Integer serviceId,
                                        @CookieParam("session_id") final String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
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

            if (serviceValidator.isEmpty(serviceId)) {
                LOGGER.error("Service id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!serviceValidator.exists(serviceId)) {
                LOGGER.error("Service id does not exist");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            final List<DoctorEntity> doctorEntities = doctorDAO.getDoctorsByServiceId(serviceId);
            final List<DoctorDTO> doctorDTOs = doctorConverter.convert(doctorEntities);
            return Response.ok(doctorDTOs).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get doctors by service id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get doctors by service id");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @GET
    @Path("/credential/{credentialId}")
    public Response getDoctorIdByCredential(@PathParam("credentialId") final Integer credentialId, @CookieParam("session_id") final String sessionId) {
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

            if (!Role.DOCTOR.toString().equalsIgnoreCase(session.getRole())) {
                LOGGER.error("Session id does not belong to doctor role [{}]", sessionIdInt);
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Forbidden to access resource. Role is not allowed.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorDTO)
                        .build();
            }

            if (credentialId == null) {
                LOGGER.error("Credential id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Credential id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (credentialId != session.getCredentialId()) {
                LOGGER.error("Credential id does not belong to existing session");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Credential id does not belong to existing session");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            final int doctorId = doctorDAO.getDoctorIdByCredentialId(credentialId);
            if (doctorId == -1) {
                LOGGER.error("Doctor id not found");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id not found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(errorDTO)
                        .build();
            }
            DoctorIdDTO doctorIdDTO = new DoctorIdDTO();
            doctorIdDTO.setId(doctorId);
            return Response.ok(doctorIdDTO).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get doctor id by credential id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get doctor id by credential id");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @POST
    public Response createDoctor(final DoctorDTO doctorDTO,
                                 @Context final UriInfo uriInfo,
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

            if (!doctorValidator.isIdEmpty(doctorDTO)) {
                LOGGER.error("Doctor id must be empty when new doctor is created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id must be empty when new doctor is created");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isFirstNameValid(doctorDTO)) {
                LOGGER.error("First name has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("First name has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isLastNameValid(doctorDTO)) {
                LOGGER.error("Last name has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Last name has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (doctorValidator.isSpecializationIdEmpty(doctorDTO)) {
                LOGGER.error("Specialization id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Specialization id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isSpecializationIdValid(doctorDTO)) {
                LOGGER.error("Specialization id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Specialization id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isEmailValid(doctorDTO)) {
                LOGGER.error("Email has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isEmailUnique(doctorDTO)) {
                LOGGER.error("Email already exists");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email already exists");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isPhoneValid(doctorDTO)) {
                LOGGER.error("Phone number has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isPhoneUnique(doctorDTO)) {
                LOGGER.error("Phone number already exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number already exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isPhotoValid(doctorDTO)) {
                LOGGER.error("Photo is not valid");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Photo is not valid");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            final DoctorEntity doctorEntity = doctorConverter.convert(doctorDTO);
            final DoctorEntity createdDoctorEntity = doctorDAO.createDoctor(doctorEntity);
            if (createdDoctorEntity == null) {
                LOGGER.error("Doctor is not created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor is not created");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            final DoctorDTO responseDoctorDTO = doctorConverter.convert(createdDoctorEntity);
            return Response.created(uriInfo.getAbsolutePathBuilder()
                            .path(String.valueOf(responseDoctorDTO.getId()))
                            .build())
                    .entity(responseDoctorDTO)
                    .build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to create doctor", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to create doctor");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @PUT
    public Response updateDoctor(final DoctorDTO doctorDTO,
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

            if (doctorValidator.isIdEmpty(doctorDTO)) {
                LOGGER.error("Doctor id must not be empty when existing doctor is updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id must not be empty when existing doctor is updated");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.idExists(doctorDTO)) {
                LOGGER.error("Doctor id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isFirstNameValid(doctorDTO)) {
                LOGGER.error("First name has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("First name has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isLastNameValid(doctorDTO)) {
                LOGGER.error("Last name has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Last name has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (doctorValidator.isSpecializationIdEmpty(doctorDTO)) {
                LOGGER.error("Specialization id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Specialization id is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isSpecializationIdValid(doctorDTO)) {
                LOGGER.error("Specialization id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Specialization id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isEmailValid(doctorDTO)) {
                LOGGER.error("Email has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isEmailUnique(doctorDTO)) {
                LOGGER.error("Email already exists");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email already exists");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isPhoneValid(doctorDTO)) {
                LOGGER.error("Phone number has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number has the wrong format");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isPhoneUnique(doctorDTO)) {
                LOGGER.error("Phone number already exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number already exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.isPhotoValid(doctorDTO)) {
                LOGGER.error("Doctor photo is not valid");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor photo is not valid");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            final DoctorEntity doctorEntity = doctorConverter.convert(doctorDTO);
            final boolean isDoctorUpdated = doctorDAO.updateDoctor(doctorEntity);
            if (!isDoctorUpdated) {
                LOGGER.error("Doctor is not updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor is not updated");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            final DoctorDTO responseDoctorDTO = doctorConverter.convert(doctorEntity);
            return Response.ok(responseDoctorDTO).build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to update doctor", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to update doctor");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }

    @DELETE
    @Path("/{doctorId}")
    public Response deleteDoctor(@PathParam("doctorId") final Integer doctorId,
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

            if (doctorValidator.isIdEmpty(doctorId)) {
                LOGGER.error("Doctor id must not be empty when existing doctor is deleted");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id must not be empty when existing doctor is deleted");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }

            if (!doctorValidator.idExists(doctorId)) {
                LOGGER.error("Doctor id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO)
                        .build();
            }
            final boolean isDoctorDeleted = doctorDAO.deleteDoctor(doctorId);
            if (!isDoctorDeleted) {
                LOGGER.error("Doctor is not deleted");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor is not deleted");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorDTO)
                        .build();
            }
            return Response.noContent().build();
        } catch (final Exception exception) {
            LOGGER.error("Error trying to delete doctor", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to delete doctor");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDTO)
                    .build();
        }
    }
}
