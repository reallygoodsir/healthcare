package com.really.good.sir.dao;

import com.really.good.sir.entity.AppointmentEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(AppointmentDAO.class);

    private static final String CREATE_APPOINTMENT =
            "INSERT INTO appointments (patient_id, doctor_id, schedule_id, status) VALUES (?, ?, ?, ?)";
    private static final String GET_ALL_APPOINTMENTS =
            "SELECT * FROM appointments";
    private static final String GET_APPOINTMENT_BY_ID =
            "SELECT * FROM appointments WHERE appointment_id = ?";
    private static final String DELETE_APPOINTMENT =
            "DELETE FROM appointments WHERE appointment_id = ?";
    private static final String UPDATE_STATUS =
            "UPDATE appointments SET status = ? WHERE appointment_id = ?";

    public AppointmentEntity createAppointment(AppointmentEntity entity) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(CREATE_APPOINTMENT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entity.getPatientId());
            ps.setInt(2, entity.getDoctorId());
            ps.setInt(3, entity.getScheduleId());
            ps.setString(4, entity.getStatus());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entity.setAppointmentId(rs.getInt(1));
            }

        } catch (SQLException e) {
            LOGGER.error("Error creating appointment", e);
        }
        return entity;
    }

    public AppointmentEntity getAppointmentById(int appointmentId) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_APPOINTMENT_BY_ID)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching appointment with id " + appointmentId, e);
        }
        return null;
    }

    public List<AppointmentEntity> getAllAppointments() {
        List<AppointmentEntity> appointments = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_ALL_APPOINTMENTS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                appointments.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching appointments", e);
        }
        return appointments;
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_STATUS)) {
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error updating appointment status for id " + appointmentId, e);
        }
        return false;
    }

    public boolean deleteAppointment(int appointmentId) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_APPOINTMENT)) {
            ps.setInt(1, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error deleting appointment with id " + appointmentId, e);
        }
        return false;
    }

    private AppointmentEntity mapRow(ResultSet rs) throws SQLException {
        AppointmentEntity entity = new AppointmentEntity();
        entity.setAppointmentId(rs.getInt("appointment_id"));
        entity.setPatientId(rs.getInt("patient_id"));
        entity.setDoctorId(rs.getInt("doctor_id"));
        entity.setScheduleId(rs.getInt("schedule_id"));
        entity.setStatus(rs.getString("status"));
        return entity;
    }
}
