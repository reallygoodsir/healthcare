package com.really.good.sir.resources;

import com.really.good.sir.dto.DoctorScheduleDTO;
import com.really.good.sir.dto.ErrorDTO;
import com.really.good.sir.dto.UserSessionDTO;
import com.really.good.sir.entity.Role;
import com.really.good.sir.service.DoctorScheduleService;
import com.really.good.sir.service.UserSessionService;
import com.really.good.sir.validator.DoctorScheduleValidator;
import com.really.good.sir.validator.DoctorValidator;
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
@RequestMapping("/doctor-schedules")
public class DoctorScheduleController {

    private static final Logger LOGGER = LogManager.getLogger(DoctorScheduleController.class);

    private final DoctorScheduleService scheduleService;
    private final UserSessionService userSessionService;
    private final DoctorScheduleValidator doctorScheduleValidator;
    private final DoctorValidator doctorValidator;

    public DoctorScheduleController(DoctorScheduleService scheduleService,
                                    UserSessionService userSessionService,
                                    DoctorScheduleValidator doctorScheduleValidator,
                                    DoctorValidator doctorValidator) {
        this.scheduleService = scheduleService;
        this.userSessionService = userSessionService;
        this.doctorScheduleValidator = doctorScheduleValidator;
        this.doctorValidator = doctorValidator;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CALL_CENTER_AGENT')")
    @GetMapping("/{doctorId}")
    public ResponseEntity<?> getSchedulesByDoctor(@PathVariable("doctorId") final Integer doctorId,
                                                  @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.CALL_CENTER_AGENT.asAuthority()) && !roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (doctorValidator.isIdEmpty(doctorId)) {
                LOGGER.error("Doctor id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No doctor id provided");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.idExists(doctorId)) {
                LOGGER.error("Doctor id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            List<DoctorScheduleDTO> schedules = scheduleService.getSchedulesByDoctor(doctorId);
            return ResponseEntity.ok(schedules);

        } catch (Exception exception) {
            LOGGER.error("Error trying to get schedules by doctor id", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get schedules by doctor id");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/appointments/{doctorId}")
    public ResponseEntity<?> getSchedulesWithAppointments(@PathVariable("doctorId") final int doctorId,
                                                          @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.DOCTOR.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (doctorValidator.isIdEmpty(doctorId)) {
                LOGGER.error("Doctor id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("No doctor id provided");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.idExists(doctorId)) {
                LOGGER.error("Doctor id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            List<DoctorScheduleDTO> schedules = scheduleService.getSchedulesForTodayWithAppointments(doctorId);
            return ResponseEntity.ok(schedules);
        } catch (Exception exception) {
            LOGGER.error("Error trying to get schedules with appointments by doctor id", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to get schedules with appointments by doctor id");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody final DoctorScheduleDTO requestScheduleDTO,
                                            @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (requestScheduleDTO.getId() != null) {
                LOGGER.error("Doctor schedule id must be empty when new schedule is created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor schedule id must be empty when new schedule is created");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (doctorValidator.isIdEmpty(requestScheduleDTO.getDoctorId())) {
                LOGGER.error("Doctor id must not be empty when new schedule is created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id must not be empty when new schedule is created");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.idExists(requestScheduleDTO.getDoctorId())) {
                LOGGER.error("Doctor id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorScheduleValidator.isScheduleDateValid(requestScheduleDTO)) {
                LOGGER.error("Date must not be in the past");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Date must not be in the past");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorScheduleValidator.isTimeRangeValid(requestScheduleDTO)) {
                LOGGER.error("Invalid start/end time");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Invalid start/end time");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (doctorScheduleValidator.isOverlapping(requestScheduleDTO)) {
                LOGGER.error("Time overlaps with an existing schedule");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Time overlaps with an existing schedule");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            DoctorScheduleDTO createdEntity = scheduleService.createSchedule(requestScheduleDTO);
            if (createdEntity == null) {
                LOGGER.error("Doctor schedule is not created");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor schedule is not created");
                return ResponseEntity.status(500).body(errorDTO);
            }

            return ResponseEntity.ok(createdEntity);
        } catch (Exception exception) {
            LOGGER.error("Error trying to create doctor schedule", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to create doctor schedule");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateSchedule(@RequestBody final DoctorScheduleDTO requestScheduleDTO,
                                            @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (doctorScheduleValidator.isScheduleIdEmpty(requestScheduleDTO)) {
                LOGGER.error("Doctor schedule id must not be empty when existing schedule is updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor schedule id must not be empty when existing schedule is updated");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorScheduleValidator.isScheduleDateValid(requestScheduleDTO)) {
                LOGGER.error("Date must not be in the past");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Date must not be in the past");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorScheduleValidator.isScheduleIdExists(requestScheduleDTO)) {
                LOGGER.error("Schedule id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Schedule id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (doctorValidator.isIdEmpty(requestScheduleDTO.getDoctorId())) {
                LOGGER.error("Doctor id must not be empty when existing schedule is updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id must not be empty when existing doctor is updated");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.idExists(requestScheduleDTO.getDoctorId())) {
                LOGGER.error("Doctor id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorScheduleValidator.isTimeRangeValid(requestScheduleDTO)) {
                LOGGER.error("Invalid start/end time");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Invalid start/end time");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (doctorScheduleValidator.isOverlapping(requestScheduleDTO)) {
                LOGGER.error("Time overlaps with an existing schedule");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Time overlaps with an existing schedule");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            DoctorScheduleDTO updatedEntity = scheduleService.updateSchedule(requestScheduleDTO);
            if (updatedEntity == null) {
                LOGGER.error("Doctor schedule is not updated");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor schedule is not updated");
                return ResponseEntity.status(500).body(errorDTO);
            }

            return ResponseEntity.ok(updatedEntity);

        } catch (Exception exception) {
            LOGGER.error("Error trying to update doctor schedule", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to update doctor schedule");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{doctorId}/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable("doctorId") final Integer doctorId,
                                            @PathVariable("scheduleId") final Integer scheduleId,
                                            @CookieValue(value = "session_id", required = false) final String sessionId) {
        try {
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//            if (!roles.contains(Role.ADMIN.asAuthority())) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(new ErrorDTO("Forbidden to access resource. Role is not allowed."));
//            }

            if (doctorScheduleValidator.isScheduleIdEmpty(scheduleId)) {
                LOGGER.error("Schedule id is empty");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Schedule id is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorScheduleValidator.isScheduleIdExists(scheduleId)) {
                LOGGER.error("Schedule id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Schedule id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (doctorValidator.isIdEmpty(doctorId)) {
                LOGGER.error("Doctor id must not be empty when existing doctor schedule is deleted");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id must not be empty when existing doctor schedule is deleted");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (!doctorValidator.idExists(doctorId)) {
                LOGGER.error("Doctor id does not exist");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor id does not exist");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            boolean isDeleted = scheduleService.deleteSchedule(scheduleId);
            if (!isDeleted) {
                LOGGER.error("Doctor schedule is not deleted");
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Doctor schedule is not deleted");
                return ResponseEntity.status(500).body(errorDTO);
            }

            return ResponseEntity.noContent().build();

        } catch (Exception exception) {
            LOGGER.error("Error trying to delete doctor schedule", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to delete doctor schedule");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }
}