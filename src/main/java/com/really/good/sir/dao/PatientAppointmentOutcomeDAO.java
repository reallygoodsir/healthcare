package com.really.good.sir.dao;

import com.really.good.sir.dto.PatientAppointmentOutcomeDTO;
import com.really.good.sir.entity.PatientAppointmentOutcomeEntity;
import com.really.good.sir.converter.PatientAppointmentOutcomeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class PatientAppointmentOutcomeDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(PatientAppointmentOutcomeDAO.class);
    private final PatientAppointmentOutcomeConverter converter = new PatientAppointmentOutcomeConverter();

    private static final String GET_BY_APPOINTMENT =
            "SELECT * FROM service_appointment_outcomes WHERE appointment_id = ?";

    private static final String INSERT_OUTCOME =
            "INSERT INTO service_appointment_outcomes (appointment_id, result) VALUES (?, ?)";

    private static final String UPDATE_OUTCOME =
            "UPDATE service_appointment_outcomes SET result = ? WHERE appointment_id = ?";

    private static final String GET_APPOINTMENT_STATUS =
            "SELECT status FROM patient_appointments WHERE appointment_id = ?";

    public PatientAppointmentOutcomeEntity getOutcomeByAppointmentId(int appointmentId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_BY_APPOINTMENT)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PatientAppointmentOutcomeEntity entity = new PatientAppointmentOutcomeEntity();
                    entity.setId(rs.getInt("id"));
                    entity.setAppointmentId(rs.getInt("appointment_id"));
                    entity.setResult(rs.getString("result"));
                    return entity;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to fetch outcome", e);
        }
        return null;
    }

    public PatientAppointmentOutcomeEntity saveOrUpdateOutcome(PatientAppointmentOutcomeDTO dto) throws Exception {
        PatientAppointmentOutcomeEntity existing = getOutcomeByAppointmentId(dto.getAppointmentId());
        PatientAppointmentOutcomeEntity entity = new PatientAppointmentOutcomeEntity();

        try (Connection conn = getConnection()) {
            if (existing == null) {
                // Insert new outcome
                try (PreparedStatement ps = conn.prepareStatement(INSERT_OUTCOME, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, dto.getAppointmentId());
                    ps.setString(2, dto.getResult());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) dto.setId(rs.getInt(1));
                    }
                }
            } else {
                // Update existing outcome
                try (PreparedStatement ps = conn.prepareStatement(UPDATE_OUTCOME)) {
                    ps.setString(1, dto.getResult());
                    ps.setInt(2, dto.getAppointmentId());
                    ps.executeUpdate();
                    dto.setId(existing.getId());
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to save/update outcome", e);
            throw new Exception("Failed to save/update outcome", e);
        }

        // Map the final state of DTO to Entity before returning
        entity.setId(dto.getId());
        entity.setAppointmentId(dto.getAppointmentId());
        entity.setResult(dto.getResult());

        return entity;
    }


    public boolean isAppointmentCompleted(int appointmentId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_APPOINTMENT_STATUS)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    return "COMPLETED".equalsIgnoreCase(status);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to check appointment status", e);
        }
        return false;
    }
}
