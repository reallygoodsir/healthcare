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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllSpecializations(
            @CookieValue(value = "session_id", required = false) String sessionId) {

        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            List<SpecializationDTO> specializations =
                    specializationService.getAllSpecializations();

            return ResponseEntity.ok(specializations);

        } catch (Exception e) {
            LOGGER.error("Error trying to get all specializations", e);
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error trying to get all specializations");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{specializationId}")
    public ResponseEntity<?> getSpecializationById(
            @PathVariable int specializationId,
            @CookieValue(value = "session_id", required = false) String sessionId) {

        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

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

    private ResponseEntity<ErrorDTO> buildError(
            HttpStatus status,
            String message) {

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(message);
        return ResponseEntity.status(status).body(errorDTO);
    }
}