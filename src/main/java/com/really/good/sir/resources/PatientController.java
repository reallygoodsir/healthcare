package com.really.good.sir.resources;

import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.PatientDTO;
import com.really.good.sir.dto.UserSessionDTO;
import com.really.good.sir.entity.Role;
import com.really.good.sir.service.PatientService;
import com.really.good.sir.service.UserSessionService;
import com.really.good.sir.validator.PatientValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private static final Logger LOGGER = LogManager.getLogger(PatientController.class);

    private final PatientValidator patientValidator;
    private final PatientService patientService;
    private final UserSessionService userSessionService;

    @Autowired
    public PatientController(PatientValidator patientValidator,
                             PatientService patientService,
                             UserSessionService userSessionService) {
        this.patientValidator = patientValidator;
        this.patientService = patientService;
        this.userSessionService = userSessionService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllPatients(@CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            List<PatientDTO> patientDTOs = patientService.getAllPatients();
            return ResponseEntity.ok(patientDTOs);
        } catch (Exception exception) {
            LOGGER.error("Error trying to get all patients", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get all patients");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/credentials/{credentialId}")
    public ResponseEntity<?> getPatientIdByCredential(@PathVariable final Integer credentialId,
                                                      @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.PATIENT.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (patientValidator.isCredentialIdEmpty(credentialId)) {
                LOGGER.error("Credential id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Credential id is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.credentialIdExists(credentialId)) {
                LOGGER.error("Credential id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Credential id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            int patientId = patientService.getPatientIdByCredentialId(credentialId);
            if (patientId == -1) {
                LOGGER.error("Patient id was not found");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id was not found");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
            }

            return ResponseEntity.ok(patientId);
        } catch (Exception exception) {
            LOGGER.error("Error trying to get patient id by credential id", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get patient id by credential id");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_CALL_CENTER_AGENT')")
    @GetMapping("/{patientId}")
    public ResponseEntity<?> getPatientById(@PathVariable final Integer patientId,
                                            @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.DOCTOR.asAuthority()) && !roles.contains(Role.CALL_CENTER_AGENT.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (patientValidator.isPatientIdEmpty(patientId)) {
                LOGGER.error("Patient id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isPatientIdExists(patientId)) {
                LOGGER.error("Patient id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            PatientDTO patientDTO = patientService.getPatientById(patientId);
            if (patientDTO == null) {
                LOGGER.error("Patient was not found");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient was not found");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
            }

            return ResponseEntity.ok(patientDTO);
        } catch (Exception exception) {
            LOGGER.error("Error trying to get patient by id", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get patient by id");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_CALL_CENTER_AGENT')")
    @GetMapping("/visits/{phoneNumber}")
    public ResponseEntity<?> getPatientByPhone(@PathVariable final String phoneNumber,
                                               @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.CALL_CENTER_AGENT.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (patientValidator.isPhoneEmpty(phoneNumber)) {
                LOGGER.error("Phone number is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isPhoneExists(phoneNumber)) {
                LOGGER.error("Phone number does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            PatientDTO patientDTO = patientService.getPatientByPhone(phoneNumber);
            if (patientDTO == null) {
                LOGGER.error("Patient was not found");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient was not found");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
            }

            return ResponseEntity.ok(patientDTO);
        } catch (Exception exception) {
            LOGGER.error("Error trying to get patient by phone number", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get patient by phone number");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody final PatientDTO requestPatientDTO,
                                           @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (!patientValidator.isPatientIdEmpty(requestPatientDTO)) {
                LOGGER.error("Patient id must be empty when new patient is created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id must be empty when new patient is created");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isFirstNameValid(requestPatientDTO)) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("First name has the wrong format");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isLastNameValid(requestPatientDTO)) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Last name has the wrong format");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isEmailValid(requestPatientDTO)) {
                LOGGER.error("Email has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email has the wrong format");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isEmailUnique(requestPatientDTO)) {
                LOGGER.error("Email already exists");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email already exists");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isPhoneValid(requestPatientDTO)) {
                LOGGER.error("Phone number has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number has the wrong format");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isPhoneUnique(requestPatientDTO)) {
                LOGGER.error("Phone number already exists");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number already exists");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isAddressValid(requestPatientDTO)) {
                LOGGER.error("No Address provided");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No Address provided");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isDateOfBirthValid(requestPatientDTO)) {
                LOGGER.error("Unfitting date of birth");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Unfitting date of birth");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            PatientDTO responsePatientDTO = patientService.createPatient(requestPatientDTO);
            if (responsePatientDTO == null) {
                LOGGER.error("Patient is not created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient is not created");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
            }

            URI location = URI.create("/healthcare/api/patients/" + responsePatientDTO.getId());
            return ResponseEntity.created(location).body(responsePatientDTO);
        } catch (Exception exception) {
            LOGGER.error("Error trying to create patient", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to create patient");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<?> updatePatient(@RequestBody final PatientDTO requestPatientDTO,
                                           @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (patientValidator.isPatientIdEmpty(requestPatientDTO)) {
                LOGGER.error("Patient id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isPatientIdExists(requestPatientDTO)) {
                LOGGER.error("Patient id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isFirstNameValid(requestPatientDTO)) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("First name has the wrong format");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isLastNameValid(requestPatientDTO)) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Last name has the wrong format");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isEmailValid(requestPatientDTO)) {
                LOGGER.error("Email has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email has the wrong format");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isEmailUnique(requestPatientDTO)) {
                LOGGER.error("Email already exists");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email already exists");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isPhoneValid(requestPatientDTO)) {
                LOGGER.error("Phone number has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number has the wrong format");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isPhoneUnique(requestPatientDTO)) {
                LOGGER.error("Phone number already exists");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number already exists");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isAddressValid(requestPatientDTO)) {
                LOGGER.error("No Address provided");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No Address provided");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isDateOfBirthValid(requestPatientDTO)) {
                LOGGER.error("Unfitting date of birth");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Unfitting date of birth");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            PatientDTO patientDTO = patientService.updatePatient(requestPatientDTO);
            if (patientDTO == null) {
                LOGGER.error("Patient is not updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient is not updated");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
            }

            return ResponseEntity.ok(patientDTO);
        } catch (Exception exception) {
            LOGGER.error("Error trying to update patient", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to update patient");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{patientId}")
    public ResponseEntity<?> deletePatient(@PathVariable final Integer patientId,
                                           @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (patientValidator.isPatientIdEmpty(patientId)) {
                LOGGER.error("Patient id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!patientValidator.isPatientIdExists(patientId)) {
                LOGGER.error("Patient id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            boolean isPatientDeleted = patientService.deletePatient(patientId);
            if (!isPatientDeleted) {
                LOGGER.error("Patient is not deleted");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Patient is not deleted");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
            }

            return ResponseEntity.noContent().build();
        } catch (Exception exception) {
            LOGGER.error("Error trying to delete patient", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to delete patient");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
        }
    }
}