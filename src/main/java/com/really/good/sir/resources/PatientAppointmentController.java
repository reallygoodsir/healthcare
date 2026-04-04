package com.really.good.sir.resources;

import com.really.good.sir.dto.*;
import com.really.good.sir.entity.Role;
import com.really.good.sir.service.PatientAppointmentOutcomeService;
import com.really.good.sir.service.PatientAppointmentService;
import com.really.good.sir.service.UserSessionService;
import com.really.good.sir.validator.*;
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
@RequestMapping("/patient-appointments")
public class PatientAppointmentController {

    private static final Logger LOGGER = LogManager.getLogger(PatientAppointmentController.class);

    private final PatientAppointmentService patientAppointmentService;
    private final PatientAppointmentOutcomeService patientAppointmentOutcomeService;
    private final UserSessionService userSessionService;
    private final PatientAppointmentValidator patientAppointmentValidator;
    private final AppointmentValidator appointmentValidator;
    private final DoctorValidator doctorValidator;
    private final PatientValidator patientValidator;
    private final PatientAppointmentOutcomeValidator outcomeValidator;
    private final ServiceValidator serviceValidator;

    public PatientAppointmentController(PatientAppointmentService patientAppointmentService,
                                        PatientAppointmentOutcomeService patientAppointmentOutcomeService,
                                        UserSessionService userSessionService,
                                        PatientAppointmentValidator patientAppointmentValidator,
                                        AppointmentValidator appointmentValidator,
                                        DoctorValidator doctorValidator,
                                        PatientValidator patientValidator,
                                        PatientAppointmentOutcomeValidator outcomeValidator,
                                        ServiceValidator serviceValidator) {
        this.patientAppointmentService = patientAppointmentService;
        this.patientAppointmentOutcomeService = patientAppointmentOutcomeService;
        this.userSessionService = userSessionService;
        this.patientAppointmentValidator = patientAppointmentValidator;
        this.appointmentValidator = appointmentValidator;
        this.doctorValidator = doctorValidator;
        this.patientValidator = patientValidator;
        this.outcomeValidator = outcomeValidator;
        this.serviceValidator = serviceValidator;
    }

    @PreAuthorize("hasRole('ROLE_CALL_CENTER_AGENT')")
    @GetMapping
    public ResponseEntity<?> getAllAppointments(@CookieValue(value = "session_id", required = false) String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.CALL_CENTER_AGENT.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            List<PatientAppointmentDTO> result = patientAppointmentService.getAllAppointments();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LOGGER.error("Error trying to get all appointments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Error trying to get all appointments"));
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/{doctorId}")
    public ResponseEntity<?> getAppointmentsByDoctorId(@PathVariable int doctorId,
                                                       @CookieValue(value = "session_id", required = false) String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.DOCTOR.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (doctorValidator.isIdEmpty(doctorId)) {
                LOGGER.error("Doctor id is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("No doctor id provided"));
            }

            if (!doctorValidator.idExists(doctorId)) {
                LOGGER.error("Doctor id does not exist");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Doctor id does not exist"));
            }

            List<PatientAppointmentDTO> result = patientAppointmentService.getAppointmentsByDoctorId(doctorId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LOGGER.error("Error trying to get appointments by doctor id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Error trying to get appointments by doctor id"));
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/{doctorId}/{date}")
    public ResponseEntity<?> getAppointmentsByDoctorIdAndDate(@PathVariable Integer doctorId,
                                                              @PathVariable String date,
                                                              @CookieValue(value = "session_id", required = false) String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.DOCTOR.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (doctorValidator.isIdEmpty(doctorId)) {
                LOGGER.error("Doctor id is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("No doctor id provided"));
            }

            if (!doctorValidator.idExists(doctorId)) {
                LOGGER.error("Doctor id does not exist");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Doctor id does not exist"));
            }

            if (!appointmentValidator.isAppointmentDateValid(date)) {
                LOGGER.error("Appointment date is not valid");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Appointment date is not valid"));
            }

            List<PatientAppointmentDTO> result = patientAppointmentService.getTodaysAppointmentsByDoctor(doctorId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LOGGER.error("Error trying to get doctor appointments for specific date", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Error trying to get doctor appointments for specific date"));
        }
    }

    @PreAuthorize("hasRole('ROLE_CALL_CENTER_AGENT')")
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody PatientAppointmentDTO dto,
                                               @CookieValue(value = "session_id", required = false) String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.CALL_CENTER_AGENT.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (!patientAppointmentValidator.isIdEmpty(dto.getAppointmentId())) {
                LOGGER.error("Appointment id must be empty when new appointment is created");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Appointment id must be empty when new appointment is created"));
            }

            if (doctorValidator.isIdEmpty(dto.getDoctorId()) || !doctorValidator.idExists(dto.getDoctorId())) {
                LOGGER.error("Doctor id is invalid");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Doctor id is empty or does not exist"));
            }

            if (patientValidator.isPatientIdEmpty(dto.getPatientId()) || !patientValidator.isPatientIdExists(dto.getPatientId())) {
                LOGGER.error("Patient id is invalid");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Patient id is empty or does not exist"));
            }

            if (serviceValidator.isEmpty(dto.getServiceId()) || !serviceValidator.exists(dto.getServiceId())) {
                LOGGER.error("Service id is invalid");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Service id is empty or does not exist"));
            }

            if (!patientAppointmentValidator.isDateValid(dto)) {
                LOGGER.error("Date must not be in the past");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Date must not be in the past"));
            }

            if (!patientAppointmentValidator.isTimeRangeValid(dto)) {
                LOGGER.error("Invalid start/end time");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Invalid start/end time"));
            }

            if (patientAppointmentValidator.isOverlapping(dto)) {
                LOGGER.error("Time overlaps with an existing schedule");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Time overlaps with an existing schedule"));
            }

            PatientAppointmentDTO created = patientAppointmentService.createAppointment(dto);
            if (created == null) {
                LOGGER.error("Appointment is not created");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorDTO("Appointment is not created"));
            }

            return ResponseEntity.ok(created);
        } catch (Exception e) {
            LOGGER.error("Error trying to create appointment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Error trying to create appointment"));
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PatchMapping("/{appointmentId}/{status}")
    public ResponseEntity<?> updateStatus(@PathVariable Integer appointmentId,
                                          @PathVariable String status,
                                          @CookieValue(value = "session_id", required = false) String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.DOCTOR.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (patientAppointmentValidator.isIdEmpty(appointmentId) || !patientAppointmentValidator.isIdExists(appointmentId)) {
                LOGGER.error("Appointment id is invalid");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Appointment id is empty or does not exist"));
            }

            if (status == null || status.isBlank() ||
                    (!status.equalsIgnoreCase("SCHEDULED") && !status.equalsIgnoreCase("COMPLETED"))) {
                LOGGER.error("Appointment status has incorrect value");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Appointment status is empty or has incorrect value"));
            }

            boolean updated = patientAppointmentService.updateStatus(appointmentId, status);
            if (!updated) {
                LOGGER.error("Appointment status is not updated");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorDTO("Appointment status is not updated"));
            }

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOGGER.error("Error trying to update appointment status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Error trying to update appointment status"));
        }
    }

    @PreAuthorize("hasRole('ROLE_CALL_CENTER_AGENT')")
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Integer appointmentId,
                                               @CookieValue(value = "session_id", required = false) String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.CALL_CENTER_AGENT.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (patientAppointmentValidator.isIdEmpty(appointmentId) || !patientAppointmentValidator.isIdExists(appointmentId)) {
                LOGGER.error("Appointment id is invalid");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Appointment id is empty or does not exist"));
            }

            boolean deleted = patientAppointmentService.deleteAppointment(appointmentId);
            if (!deleted) {
                LOGGER.error("Appointment is not deleted");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorDTO("Appointment is not deleted"));
            }

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOGGER.error("Error trying to delete appointment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Error trying to delete appointment"));
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/status/{appointmentId}")
    public ResponseEntity<?> getAppointmentStatus(@PathVariable Integer appointmentId,
                                                  @CookieValue(value = "session_id", required = false) String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.DOCTOR.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (patientAppointmentValidator.isIdEmpty(appointmentId) || !patientAppointmentValidator.isIdExists(appointmentId)) {
                LOGGER.error("Appointment id is invalid");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Appointment id is empty or does not exist"));
            }

            String status = patientAppointmentService.getAppointmentStatusById(appointmentId);
            if (status == null || status.isBlank()) {
                LOGGER.error("Appointment status not found");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorDTO("Appointment status not found"));
            }

            return ResponseEntity.ok(new AppointmentDTO(appointmentId, status));
        } catch (Exception e) {
            LOGGER.error("Error trying to get appointment status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Error trying to get appointment status"));
        }
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/patient/{patientId}/details")
    public ResponseEntity<?> getAppointmentDetailsByPatient(@PathVariable Integer patientId,
                                                            @CookieValue(value = "session_id", required = false) String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.PATIENT.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (patientValidator.isPatientIdEmpty(patientId) || !patientValidator.isPatientIdExists(patientId)) {
                LOGGER.error("Patient id is invalid");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Patient id is empty or does not exist"));
            }

            List<PatientAppointmentDetailsDTO> list = patientAppointmentService.getAppointmentDetailsByPatientId(patientId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            LOGGER.error("Error trying to get appointment details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Error trying to get appointment details"));
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/outcome/{appointmentId}")
    public ResponseEntity<?> getOutcome(@PathVariable Integer appointmentId,
                                        @CookieValue(value = "session_id", required = false) String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.DOCTOR.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (patientAppointmentValidator.isIdEmpty(appointmentId) || !patientAppointmentValidator.isIdExists(appointmentId)) {
                LOGGER.error("Appointment id is invalid");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Appointment id is empty or does not exist"));
            }

            PatientAppointmentOutcomeDTO dto = patientAppointmentOutcomeService.getOutcomeByAppointmentId(appointmentId);
            if (dto == null) {
                LOGGER.error("Appointment outcome not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorDTO("Appointment outcome not found"));
            }

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            LOGGER.error("Error trying to get appointment outcome", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Error trying to get appointment outcome"));
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PutMapping("/outcome")
    public ResponseEntity<?> saveOrUpdateOutcome(@RequestBody PatientAppointmentOutcomeDTO dto,
                                                 @CookieValue(value = "session_id", required = false) String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.DOCTOR.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (outcomeValidator.isOutcomeIdEmpty(dto)) {
                LOGGER.error("Outcome id is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Outcome id is empty"));
            }

            if (patientAppointmentValidator.isIdEmpty(dto.getAppointmentId()) ||
                    !patientAppointmentValidator.isIdExists(dto.getAppointmentId())) {
                LOGGER.error("Appointment id is invalid");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Appointment id is empty or does not exist"));
            }

            if (!outcomeValidator.isResultValid(dto)) {
                LOGGER.error("Appointment outcome result is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorDTO("Appointment outcome result is empty"));
            }

            PatientAppointmentOutcomeDTO updated = patientAppointmentOutcomeService.saveOrUpdateOutcome(dto);
            if (updated == null) {
                LOGGER.error("Appointment outcome was not updated");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorDTO("Appointment outcome was not updated"));
            }

            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            LOGGER.error("Error trying to update appointment outcome", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Error trying to update appointment outcome"));
        }
    }
}