package com.really.good.sir.resources;

import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.ServiceDTO;
import com.really.good.sir.dto.UserSessionDTO;
import com.really.good.sir.entity.Role;
import com.really.good.sir.service.ServiceService;
import com.really.good.sir.service.UserSessionService;
import com.really.good.sir.validator.ServiceValidator;
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
@RequestMapping("/services")
public class ServiceController {

    private static final Logger LOGGER =
            LogManager.getLogger(ServiceController.class);

    private final ServiceService service;
    private final UserSessionService userSessionService;
    private final ServiceValidator serviceValidator;

    public ServiceController(ServiceService service,
                             UserSessionService userSessionService,
                             ServiceValidator serviceValidator) {
        this.service = service;
        this.userSessionService = userSessionService;
        this.serviceValidator = serviceValidator;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CALL_CENTER_AGENT')")
    @GetMapping
    public ResponseEntity<?> getAllServices(
            @CookieValue(value = "session_id", required = false) String sessionId) {

        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.CALL_CENTER_AGENT.asAuthority()) && !roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            List<ServiceDTO> dtos = service.getAllServices();
            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            LOGGER.error("Error trying to get all services", e);
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error trying to get all services");
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CALL_CENTER_AGENT', 'ROLE_DOCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(
            @PathVariable Integer id,
            @CookieValue(value = "session_id", required = false) String sessionId) {

        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.CALL_CENTER_AGENT.asAuthority()) && !roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (serviceValidator.isEmpty(id))
                return buildError(HttpStatus.BAD_REQUEST, "Service id is empty");

            if (!serviceValidator.exists(id))
                return buildError(HttpStatus.BAD_REQUEST, "Service id does not exist");

            ServiceDTO dto = service.getServiceById(id);
            if (dto == null)
                return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Service was not found");

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            LOGGER.error("Error trying to get service by id", e);
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error trying to get service by id");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createService(
            @RequestBody ServiceDTO dto,
            @CookieValue(value = "session_id", required = false) String sessionId) {

        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (!serviceValidator.isNameValid(dto))
                return buildError(HttpStatus.BAD_REQUEST,
                        "Service name has the wrong format");

            if (!serviceValidator.isNameUnique(dto))
                return buildError(HttpStatus.BAD_REQUEST,
                        "Service name already exists");

            if (!serviceValidator.isPriceValid(dto))
                return buildError(HttpStatus.BAD_REQUEST,
                        "Price has to be over 0");

            ServiceDTO created = service.createService(dto);
            if (created == null)
                return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Service is not created");

            return ResponseEntity.ok(created);

        } catch (Exception e) {
            LOGGER.error("Error trying to create new service", e);
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error trying to create new service");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateService(
            @RequestBody ServiceDTO dto,
            @CookieValue(value = "session_id", required = false) String sessionId) {

        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (!serviceValidator.isNameValid(dto))
                return buildError(HttpStatus.BAD_REQUEST,
                        "Service name has the wrong format");

            if (!serviceValidator.isNameUnique(dto))
                return buildError(HttpStatus.BAD_REQUEST,
                        "Service name already exists");

            if (!serviceValidator.isPriceValid(dto))
                return buildError(HttpStatus.BAD_REQUEST,
                        "Price has to be over 0");

            ServiceDTO result = service.updateService(dto);
            if (result == null)
                return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Service is not updated");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            LOGGER.error("Error trying to update existing service", e);
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error trying to update existing service");
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CALL_CENTER_AGENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(
            @PathVariable Integer id,
            @CookieValue(value = "session_id", required = false) String sessionId) {

        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.CALL_CENTER_AGENT.asAuthority()) && !roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (serviceValidator.isEmpty(id))
                return buildError(HttpStatus.BAD_REQUEST,
                        "No service id provided");

            if (!serviceValidator.exists(id))
                return buildError(HttpStatus.BAD_REQUEST,
                        "Service id does not exist");

            boolean deleted = service.deleteService(id);
            if (!deleted)
                return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Service is not deleted");

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            LOGGER.error("Error trying to delete service by id", e);
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error trying to delete service by id");
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