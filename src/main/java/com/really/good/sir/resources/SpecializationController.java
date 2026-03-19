package com.really.good.sir.resources;

import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.SpecializationDTO;
import com.really.good.sir.dto.UserSessionDTO;
import com.really.good.sir.entity.Role;
import com.really.good.sir.service.SpecializationService;
import com.really.good.sir.service.UserSessionService;
import com.really.good.sir.validator.SpecializationValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specializations")
public class SpecializationController {

    private static final Logger LOGGER =
            LogManager.getLogger(SpecializationController.class);

    private final SpecializationService specializationService;
    private final UserSessionService userSessionService;
    private final SpecializationValidator specializationValidator;

    public SpecializationController(SpecializationService specializationService,
                                    UserSessionService userSessionService,
                                    SpecializationValidator specializationValidator) {
        this.specializationService = specializationService;
        this.userSessionService = userSessionService;
        this.specializationValidator = specializationValidator;
    }

    @GetMapping
    public ResponseEntity<?> getAllSpecializations(
            @CookieValue(value = "session_id", required = false) String sessionId) {

        try {

            ResponseEntity<ErrorDTO> authError =
                    validateSession(sessionId, Role.ADMIN);
            if (authError != null) return authError;

            List<SpecializationDTO> specializations =
                    specializationService.getAllSpecializations();

            return ResponseEntity.ok(specializations);

        } catch (Exception e) {
            LOGGER.error("Error trying to get all specializations", e);
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error trying to get all specializations");
        }
    }

    @GetMapping("/{specializationId}")
    public ResponseEntity<?> getSpecializationById(
            @PathVariable int specializationId,
            @CookieValue(value = "session_id", required = false) String sessionId) {

        try {

            ResponseEntity<ErrorDTO> authError =
                    validateSession(sessionId, Role.ADMIN, Role.CALL_CENTER_AGENT);
            if (authError != null) return authError;

            if (specializationValidator.isEmpty(specializationId)) {
                LOGGER.error("Specialization id is empty");
                return buildError(HttpStatus.BAD_REQUEST,
                        "No specialization id provided");
            }

            if (!specializationValidator.exists(specializationId)) {
                LOGGER.error("Specialization id does not exist");
                return buildError(HttpStatus.BAD_REQUEST,
                        "Specialization id does not exist");
            }

            SpecializationDTO specialization =
                    specializationService.getSpecializationById(specializationId);

            if (specialization == null) {
                LOGGER.error("Specialization was not found");
                return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Specialization was not found");
            }

            return ResponseEntity.ok(specialization);

        } catch (Exception e) {
            LOGGER.error("Error trying to get specialization by id", e);
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error trying to get specialization by id");
        }
    }

    private ResponseEntity<ErrorDTO> validateSession(
            String sessionId,
            Role... allowedRoles) {

        if (sessionId == null || sessionId.isEmpty()) {
            LOGGER.error("Session id is empty");
            return buildError(HttpStatus.UNAUTHORIZED,
                    "Session id is empty");
        }

        int sessionIdInt;
        try {
            sessionIdInt = Integer.parseInt(sessionId);
        } catch (NumberFormatException e) {
            LOGGER.error("Session id has invalid format", e);
            return buildError(HttpStatus.UNAUTHORIZED,
                    "Not authorized. Session id has incorrect format");
        }

        UserSessionDTO session =
                userSessionService.getSessionById(sessionIdInt);

        if (session == null) {
            LOGGER.error("Session id does not exist [{}]", sessionId);
            return buildError(HttpStatus.UNAUTHORIZED,
                    "Not authorized. Session id does not exist");
        }

        boolean allowed = false;
        for (Role role : allowedRoles) {
            if (role.toString().equalsIgnoreCase(session.getRole())) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            LOGGER.error("Role [{}] not allowed", session.getRole());
            return buildError(HttpStatus.FORBIDDEN,
                    "Forbidden to access resource. Role is not allowed.");
        }

        return null;
    }

    private ResponseEntity<ErrorDTO> buildError(
            HttpStatus status,
            String message) {

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(message);
        return ResponseEntity.status(status).body(errorDTO);
    }
}