package com.really.good.sir.resources;

import com.really.good.sir.dto.AppointmentDTO;
import com.really.good.sir.dto.AppointmentOutcomeDTO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.entity.Role;
import com.really.good.sir.service.AppointmentOutcomeService;
import com.really.good.sir.service.AppointmentService;
import com.really.good.sir.service.UserSessionService;
import com.really.good.sir.validator.AppointmentOutcomeValidator;
import com.really.good.sir.validator.AppointmentValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private static final Logger LOGGER = LogManager.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;
    private final AppointmentOutcomeService outcomeService;
    private final UserSessionService userSessionService;
    private final AppointmentOutcomeValidator patientValidator;
    private final AppointmentValidator appointmentValidator;

    public AppointmentController(AppointmentService appointmentService,
                                 AppointmentOutcomeService outcomeService,
                                 UserSessionService userSessionService,
                                 AppointmentOutcomeValidator patientValidator,
                                 AppointmentValidator appointmentValidator) {
        this.appointmentService = appointmentService;
        this.outcomeService = outcomeService;
        this.userSessionService = userSessionService;
        this.patientValidator = patientValidator;
        this.appointmentValidator = appointmentValidator;
    }

    //    @PreAuthorize("hasRole('ROLE_CALL_CENTER_AGENT')")
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentDTO appointmentDTO,
                                               @CookieValue(value = "session_id", required = false) String sessionId) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        if (!roles.contains(Role.CALL_CENTER_AGENT.asAuthority())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
        }

        // Validation
        if (!appointmentValidator.isAppointmentIdEmpty(appointmentDTO)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("The appointment should have no preexisting appointment id"));
        }
        if (!appointmentValidator.isStatusValid(appointmentDTO)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("The appointment should have no preexisting status"));
        }
        if (appointmentValidator.isPatientIdEmpty(appointmentDTO)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("No patient id provided"));
        }
        if (appointmentValidator.isPatientIdInvalid(appointmentDTO)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Incorrect patient id provided"));
        }
        if (appointmentValidator.isDoctorIdEmpty(appointmentDTO)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("No doctor id provided"));
        }
        if (appointmentValidator.isDoctorIdInvalid(appointmentDTO)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Incorrect doctor id provided"));
        }
        if (appointmentValidator.isScheduleIdEmpty(appointmentDTO)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("No schedule id provided"));
        }
        if (!appointmentValidator.isScheduleIdValid(appointmentDTO)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Incorrect schedule id provided"));
        }

        appointmentDTO.setStatus("SCHEDULED");
        AppointmentDTO created = appointmentService.createAppointment(appointmentDTO);
        if (created == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Failed to create an appointment"));
        }

        return ResponseEntity.ok(created);
    }

    @PutMapping
    public ResponseEntity<?> updateAppointmentOutcome(@RequestBody AppointmentOutcomeDTO appointmentOutcomeDTO,
                                                      @CookieValue(value = "session_id", required = false) String sessionId) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        if (!roles.contains(Role.DOCTOR.asAuthority())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
        }

        if (patientValidator.isAppointmentIdEmpty(appointmentOutcomeDTO)) {
            return ResponseEntity.badRequest().body(new ErrorDTO("No appointment id provided"));
        }
        if (!appointmentValidator.isAppointmentIdValid(appointmentOutcomeDTO)) {
            return ResponseEntity.badRequest().body(new ErrorDTO("Incorrect appointment id provided"));
        }
        if (!patientValidator.isDiagnosisValid(appointmentOutcomeDTO)) {
            return ResponseEntity.badRequest().body(new ErrorDTO("No diagnosis provided"));
        }
        if (!patientValidator.isRecommendationsValid(appointmentOutcomeDTO)) {
            return ResponseEntity.badRequest().body(new ErrorDTO("No recommendation provided"));
        }

        try {
            AppointmentOutcomeDTO outcome = outcomeService.saveOrUpdateOutcome(appointmentOutcomeDTO);
            return ResponseEntity.ok(outcome);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Error during appointment outcome processing"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllAppointments(@CookieValue(value = "session_id", required = false) String sessionId) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        if (!roles.contains(Role.CALL_CENTER_AGENT.asAuthority()) && !roles.contains(Role.DOCTOR.asAuthority())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
        }

        List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<?> getAppointmentById(@PathVariable int appointmentId,
                                                @CookieValue(value = "session_id", required = false) String sessionId) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        if (!roles.contains(Role.DOCTOR.asAuthority())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
        }

        AppointmentDTO appointment = appointmentService.getAppointmentById(appointmentId);
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Failed to get an appointment by id"));
        }

        return ResponseEntity.ok(appointment);
    }

    // doctor
    @GetMapping("/{appointmentId}/outcome")
    public ResponseEntity<?> getAppointmentOutcome(@PathVariable int appointmentId,
                                                   @CookieValue(value = "session_id", required = false) String sessionId) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        if (!roles.contains(Role.DOCTOR.asAuthority())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
        }

        AppointmentOutcomeDTO outcome = outcomeService.getOutcomeByAppointmentId(appointmentId);
        if (outcome == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Failed to get an appointment outcome"));
        }

        return ResponseEntity.ok(outcome);
    }

    @PatchMapping("/{appointmentId}/{status}")
    public ResponseEntity<?> updateStatus(@PathVariable int appointmentId,
                                          @PathVariable String status,
                                          @CookieValue(value = "session_id", required = false) String sessionId) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        if (!roles.contains(Role.DOCTOR.asAuthority())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
        }

        if (!appointmentValidator.isAppointmentIdValid(appointmentId)) {
            return ResponseEntity.badRequest().body(new ErrorDTO("Appointment id not found"));
        }

        if (!appointmentValidator.isStatusValid(status)) {
            return ResponseEntity.badRequest().body(new ErrorDTO("Invalid status provided"));
        }

        boolean success = appointmentService.updateAppointmentStatus(appointmentId, status);
        if (!success) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Failed to update status"));
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable int appointmentId,
                                               @CookieValue(value = "session_id", required = false) String sessionId) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        if (!roles.contains(Role.CALL_CENTER_AGENT.asAuthority())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
        }

        if (!appointmentValidator.isAppointmentIdValid(appointmentId)) {
            return ResponseEntity.badRequest().body(new ErrorDTO("Appointment id not found"));
        }

        boolean deleted = appointmentService.deleteAppointment(appointmentId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Failed to delete an appointment"));
        }

        return ResponseEntity.noContent().build();
    }
}