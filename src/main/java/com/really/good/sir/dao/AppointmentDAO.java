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

    private static final String GET_APPOINTMENTS_BY_DOCTOR =
            "SELECT * FROM appointments WHERE doctor_id = ?";

    private static final String GET_APPOINTMENT_BY_IDS =
            "SELECT * FROM appointments WHERE doctor_id = ? AND schedule_id = ?";

    private static final String DELETE_APPOINTMENT =
            "DELETE FROM appointments WHERE appointment_id = ?";

    public AppointmentEntity createAppointment(AppointmentEntity entity) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(CREATE_APPOINTMENT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entity.getPatientId());
            ps.setInt(2, entity.getDoctorId());
            ps.setInt(3, entity.getScheduleId());
            ps.setString(4, entity.getStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setAppointmentId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error creating appointment", e);
        }
        return entity;
    }

    public List<AppointmentEntity> getAppointmentsByDoctor(int doctorId) {
        List<AppointmentEntity> appointments = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_APPOINTMENTS_BY_DOCTOR)) {

            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AppointmentEntity entity = new AppointmentEntity();
                    entity.setAppointmentId(rs.getInt("appointment_id"));
                    entity.setPatientId(rs.getInt("patient_id"));
                    entity.setDoctorId(rs.getInt("doctor_id"));
                    entity.setScheduleId(rs.getInt("schedule_id"));
                    entity.setStatus(rs.getString("status"));
                    appointments.add(entity);
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error fetching appointments for doctor " + doctorId, e);
        }
        return appointments;
    }

    public List<AppointmentEntity> getAppointmentByIds(int doctorId, int scheduleId) {
        List<AppointmentEntity> appointments = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_APPOINTMENT_BY_IDS)) {

            ps.setInt(1, doctorId);
            ps.setInt(2, scheduleId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AppointmentEntity entity = new AppointmentEntity();
                    entity.setAppointmentId(rs.getInt("appointment_id"));
                    entity.setPatientId(rs.getInt("patient_id"));
                    entity.setDoctorId(rs.getInt("doctor_id"));
                    entity.setScheduleId(rs.getInt("schedule_id"));
                    entity.setStatus(rs.getString("status"));
                    appointments.add(entity);
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error fetching appointment by doctorId " + doctorId + " and scheduleId " + scheduleId, e);
        }
        return appointments;
    }

    public boolean deleteAppointment(int appointmentId) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_APPOINTMENT)) {

            ps.setInt(1, appointmentId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.error("Error deleting appointment with id " + appointmentId, e);
        }
        return false;
    }
}
