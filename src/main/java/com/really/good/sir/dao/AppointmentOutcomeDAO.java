package com.really.good.sir.dao;

import com.really.good.sir.dto.AppointmentOutcomeDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class AppointmentOutcomeDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(AppointmentOutcomeDAO.class);

    private static final String GET_OUTCOME_BY_APPOINTMENT =
            "SELECT * FROM appointment_outcomes WHERE appointment_id = ?";

    private static final String INSERT_OUTCOME =
            "INSERT INTO appointment_outcomes (appointment_id, diagnosis, recommendations, created_at, updated_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

    private static final String UPDATE_OUTCOME =
            "UPDATE appointment_outcomes SET diagnosis = ?, recommendations = ?, updated_at = CURRENT_TIMESTAMP WHERE appointment_id = ?";

    public AppointmentOutcomeDTO saveOrUpdateOutcome(AppointmentOutcomeDTO dto) {
        try (Connection conn = getConnection()) {
            // check if outcome exists
            boolean exists;
            try (PreparedStatement ps = conn.prepareStatement(GET_OUTCOME_BY_APPOINTMENT)) {
                ps.setInt(1, dto.getAppointmentId());
                try (ResultSet rs = ps.executeQuery()) {
                    exists = rs.next();
                }
            }

            if (exists) {
                try (PreparedStatement ps = conn.prepareStatement(UPDATE_OUTCOME)) {
                    ps.setString(1, dto.getDiagnosis());
                    ps.setString(2, dto.getRecommendations());
                    ps.setInt(3, dto.getAppointmentId());
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(INSERT_OUTCOME, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, dto.getAppointmentId());
                    ps.setString(2, dto.getDiagnosis());
                    ps.setString(3, dto.getRecommendations());
                    ps.executeUpdate();
                }
            }

            // return the latest outcome
            return getOutcomeByAppointmentId(dto.getAppointmentId());

        } catch (SQLException e) {
            LOGGER.error("Failed to save/update appointment outcome", e);
            throw new RuntimeException(e);
        }
    }

    public AppointmentOutcomeDTO getOutcomeByAppointmentId(int appointmentId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_OUTCOME_BY_APPOINTMENT)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AppointmentOutcomeDTO dto = new AppointmentOutcomeDTO();
                    dto.setAppointmentId(rs.getInt("appointment_id"));
                    dto.setDiagnosis(rs.getString("diagnosis"));
                    dto.setRecommendations(rs.getString("recommendations"));
                    dto.setFollowUpRequired(rs.getBoolean("follow_up_required"));
                    dto.setFollowUpDate(rs.getDate("follow_up_date"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    dto.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to get appointment outcome", e);
        }
        return null;
    }
}
