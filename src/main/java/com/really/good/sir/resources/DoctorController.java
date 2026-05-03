package com.really.good.sir.resources;

import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.dto.DoctorIdDTO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.UserSessionDTO;
import com.really.good.sir.service.DoctorService;
import com.really.good.sir.service.UserSessionService;
import com.really.good.sir.validator.DoctorValidator;
import com.really.good.sir.validator.ServiceValidator;
import com.really.good.sir.validator.ValidationResult;
import com.really.good.sir.validator.pipeline.DoctorValidationPipeline;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private static final Logger LOGGER = LogManager.getLogger(DoctorController.class);

    private final DoctorService doctorService;
    private final UserSessionService userSessionService;
    private final DoctorValidator doctorValidator;
    private final ServiceValidator serviceValidator;
    private final DoctorValidationPipeline doctorValidationPipeline;

    public DoctorController(DoctorService doctorService,
                            UserSessionService userSessionService,
                            DoctorValidator doctorValidator,
                            ServiceValidator serviceValidator,
                            DoctorValidationPipeline doctorValidationPipeline) {
        this.doctorService = doctorService;
        this.userSessionService = userSessionService;
        this.doctorValidator = doctorValidator;
        this.serviceValidator = serviceValidator;
        this.doctorValidationPipeline = doctorValidationPipeline;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CALL_CENTER_AGENT')")
    @GetMapping

    public ResponseEntity<?> getAllDoctors(@CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//        if (true) throw new RuntimeException("global exception handler test");
        final List<DoctorDTO> doctorDTOs = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctorDTOs);
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get all doctors", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get all doctors");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_CALL_CENTER_AGENT')")
    @GetMapping("/{doctorId}")

    public ResponseEntity<?> getDoctor(@PathVariable final Integer doctorId,
                                       @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.CALL_CENTER_AGENT.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (doctorValidator.isIdEmpty(doctorId)) {
                LOGGER.error("Doctor id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No doctor id provided");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.idExists(doctorId)) {
                LOGGER.error("Doctor id does not exist");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            final DoctorDTO doctorDTO = doctorService.getDoctorById(doctorId);
            return ResponseEntity.ok(doctorDTO);
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get doctor by id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get doctor by id");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_CALL_CENTER_AGENT')")
    @GetMapping("/service/{serviceId}")

    public ResponseEntity<?> getDoctorsByService(@PathVariable final Integer serviceId,
                                                 @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.CALL_CENTER_AGENT.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (serviceValidator.isEmpty(serviceId)) {
                LOGGER.error("Service id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service id is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!serviceValidator.exists(serviceId)) {
                LOGGER.error("Service id does not exist");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Service id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            final List<DoctorDTO> doctorDTOs = doctorService.getDoctorsByServiceId(serviceId);
            return ResponseEntity.ok(doctorDTOs);
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get doctors by service id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get doctors by service id");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/credential/{credentialId}")

    public ResponseEntity<?> getDoctorIdByCredential(@PathVariable final Integer credentialId,
                                                     @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserSessionDTO session = (UserSessionDTO) authentication.getPrincipal();
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.DOCTOR.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }


            if (credentialId == null) {
                LOGGER.error("Credential id is empty");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Credential id is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!credentialId.equals(session.getCredentialId())) {
                LOGGER.error("Credential id does not belong to existing session");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Credential id does not belong to existing session");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            final int doctorId = doctorService.getDoctorIdByCredentialId(credentialId);
            if (doctorId == -1) {
                LOGGER.error("Doctor id not found");
                final ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id not found");
                return ResponseEntity.status(404).body(errorDTO);
            }

            DoctorIdDTO doctorIdDTO = new DoctorIdDTO();
            doctorIdDTO.setId(doctorId);
            return ResponseEntity.ok(doctorIdDTO);
        } catch (final Exception exception) {
            LOGGER.error("Error trying to get doctor id by credential id", exception);
            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get doctor id by credential id");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createDoctor(@RequestBody final DoctorDTO doctorDTO,
                                          @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
            ValidationResult validationResult = doctorValidationPipeline.validate(doctorDTO);

            if (!validationResult.isValid()) {
                LOGGER.error(validationResult.getMessage());

                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage(validationResult.getMessage());

                return ResponseEntity.badRequest().body(errorDTO);
            }

            DoctorDTO doctor = doctorService.createDoctor(doctorDTO);

            if (doctor == null) {
                LOGGER.error("Doctor is not created");

                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor is not created");

                return ResponseEntity.status(500).body(errorDTO);
            }

            return ResponseEntity
                    .created(null)
                    .body(doctor);
        } catch (final Exception exception) {
            LOGGER.error("Error trying to create doctor", exception);

            final ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to create doctor");

            return ResponseEntity.status(500).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping

    public ResponseEntity<?> updateDoctor(@RequestBody final DoctorDTO doctorDTO,
                                          @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (doctorValidator.isIdEmpty(doctorDTO)) {
                LOGGER.error("Doctor id must not be empty when existing doctor is updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id must not be empty when existing doctor is updated");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.idExists(doctorDTO)) {
                LOGGER.error("Doctor id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.isFirstNameValid(doctorDTO)) {
                LOGGER.error("First name has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("First name has the wrong format");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.isLastNameValid(doctorDTO)) {
                LOGGER.error("Last name has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Last name has the wrong format");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (doctorValidator.isSpecializationIdEmpty(doctorDTO)) {
                LOGGER.error("Specialization id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Specialization id is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.isSpecializationIdValid(doctorDTO)) {
                LOGGER.error("Specialization id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Specialization id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.isEmailValid(doctorDTO)) {
                LOGGER.error("Email has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email has the wrong format");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.isEmailUnique(doctorDTO)) {
                LOGGER.error("Email already exists");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email already exists");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.isPhoneValid(doctorDTO)) {
                LOGGER.error("Phone number has the wrong format");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number has the wrong format");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.isPhoneUnique(doctorDTO)) {
                LOGGER.error("Phone number already exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Phone number already exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.isPhotoValid(doctorDTO)) {
                LOGGER.error("Doctor photo is not valid");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor photo is not valid");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            DoctorDTO updatedDoctorDTO = doctorService.updateDoctor(doctorDTO);
            if (updatedDoctorDTO == null) {
                LOGGER.error("Doctor is not updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor is not updated");
                return ResponseEntity.status(500).body(errorDTO);
            }

            return ResponseEntity.ok(updatedDoctorDTO);

        } catch (Exception exception) {
            LOGGER.error("Error trying to update doctor", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to update doctor");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{doctorId}")

    public ResponseEntity<?> deleteDoctor(@PathVariable final Integer doctorId,
                                          @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (doctorValidator.isIdEmpty(doctorId)) {
                LOGGER.error("Doctor id must not be empty when existing doctor is deleted");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id must not be empty when existing doctor is deleted");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.idExists(doctorId)) {
                LOGGER.error("Doctor id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            final boolean isDoctorDeleted = doctorService.deleteDoctor(doctorId);
            if (!isDoctorDeleted) {
                LOGGER.error("Doctor is not deleted");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor is not deleted");
                return ResponseEntity.status(500).body(errorDTO);
            }

            return ResponseEntity.noContent().build();

        } catch (Exception exception) {
            LOGGER.error("Error trying to delete doctor", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to delete doctor");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }
}