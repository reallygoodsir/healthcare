package com.really.good.sir.resources;

import com.really.good.sir.converter.UserSessionConverter;
import com.really.good.sir.dto.*;
import com.really.good.sir.service.UserSessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authorization")
public class AuthorizationController {

    private static final Logger LOGGER = LogManager.getLogger(AuthorizationController.class);

    private final UserSessionService userSessionService;
    private final UserSessionConverter converter;

    public AuthorizationController(UserSessionService userSessionService,
                                   UserSessionConverter converter) {
        this.userSessionService = userSessionService;
        this.converter = converter;
    }

    @PostMapping("/")
    public ResponseEntity<?> authorize(@RequestBody LoginRequestDTO request) {
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        try {
            if (request == null) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Login request is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Email is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Password is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            UserSessionDTO sessionDTO =
                    userSessionService.authorize(request.getEmail(), request.getPassword());

            if (sessionDTO == null) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Credentials are not valid");
                return ResponseEntity.status(401).body(errorDTO);
            }

            ResponseCookie cookie = ResponseCookie.from("session_id", String.valueOf(sessionDTO.getId()))
                    .path("/")
                    .maxAge(30 * 60)
                    .httpOnly(true)
                    .build();

            LOGGER.info("User logged in with credential_id {} and role {}",
                    sessionDTO.getCredentialId(), sessionDTO.getRole());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(sessionDTO);

        } catch (Exception exception) {
            LOGGER.error("Error trying to authorize the user", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to authorize the user");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }

    @PostMapping("/session")
    public ResponseEntity<?> checkSession(@RequestBody SessionCheckRequestDTO request) {
        try {
            if (request == null) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session request is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            if (request.getSessionId() == null) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            UserSessionDTO sessionEntity =
                    userSessionService.getSessionById(request.getSessionId());

            if (sessionEntity == null) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is not valid");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            long now = System.currentTimeMillis();
            long loginTime = sessionEntity.getLoginDateTime().getTime();
            long elapsedMinutes = (now - loginTime) / (1000 * 60);

            if (elapsedMinutes > 30) {
                ResponseCookie deleteCookie = ResponseCookie.from("session_id", "")
                        .path("/")
                        .maxAge(0)
                        .httpOnly(true)
                        .build();
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                        .body(new UserSessionDTO());
            }

            return ResponseEntity.ok(converter.convert(sessionEntity));

        } catch (Exception exception) {
            LOGGER.error("Error trying to check the session", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to check the session");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }

    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<?> deleteSession(@PathVariable Integer sessionId) {
        try {
            if (sessionId == null) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is empty");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            UserSessionDTO sessionEntity =
                    userSessionService.getSessionById(sessionId);

            if (sessionEntity == null) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session id is not valid");
                return ResponseEntity.badRequest().body(errorDTO);
            }

            boolean deleted =
                    userSessionService.deleteSessionById(sessionId);

            if (!deleted) {
                LOGGER.error("Session was not deleted with id {}", sessionId);
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setMessage("Session was not deleted");
                return ResponseEntity.status(500).body(errorDTO);
            }

            LOGGER.info("Deleted user session with id {}", sessionId);

            ResponseCookie expiredCookie = ResponseCookie.from("session_id", "")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .build();

            return ResponseEntity.noContent()
                    .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                    .build();

        } catch (Exception exception) {
            LOGGER.error("Error trying to delete the user", exception);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage("Error trying to delete the user");
            return ResponseEntity.status(500).body(errorDTO);
        }
    }
}