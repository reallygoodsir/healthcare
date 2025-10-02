package com.really.good.sir.dao;

import com.really.good.sir.entity.PatientAppointmentEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientAppointmentDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(PatientAppointmentDAO.class);

    private static final String CREATE_APPOINTMENT =
            "INSERT INTO patient_appointments (patient_id, service_id, doctor_id, date, start_time, end_time, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String DELETE_APPOINTMENT =
            "DELETE FROM patient_appointments WHERE appointment_id = ?";

    private static final String GET_ALL_APPOINTMENTS =
            "SELECT pa.*, p.first_name AS patient_first_name, p.last_name AS patient_last_name, s.name AS service_name " +
                    "FROM patient_appointments pa " +
                    "JOIN patients p ON pa.patient_id = p.patient_id " +
                    "JOIN service s ON pa.service_id = s.id";

    public PatientAppointmentEntity createAppointment(PatientAppointmentEntity entity) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(CREATE_APPOINTMENT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entity.getPatientId());
            ps.setInt(2, entity.getServiceId());
            ps.setInt(3, entity.getDoctorId()); // <-- Added doctor_id
            ps.setDate(4, entity.getDate());
            ps.setTime(5, entity.getStartTime());
            ps.setTime(6, entity.getEndTime());
            ps.setString(7, entity.getStatus());

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

    public boolean deleteAppointment(int appointmentId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_APPOINTMENT)) {
            ps.setInt(1, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error deleting appointment", e);
            return false;
        }
    }

    public List<PatientAppointmentEntity> getAllAppointments() {
        List<PatientAppointmentEntity> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL_APPOINTMENTS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PatientAppointmentEntity entity = new PatientAppointmentEntity();
                entity.setAppointmentId(rs.getInt("appointment_id"));
                entity.setPatientId(rs.getInt("patient_id"));
                entity.setServiceId(rs.getInt("service_id"));
                entity.setDoctorId(rs.getInt("doctor_id")); // <-- Retrieve doctor_id
                entity.setDate(rs.getDate("date"));
                entity.setStartTime(rs.getTime("start_time"));
                entity.setEndTime(rs.getTime("end_time"));
                entity.setStatus(rs.getString("status"));

                entity.setPatientFirstName(rs.getString("patient_first_name"));
                entity.setPatientLastName(rs.getString("patient_last_name"));
                entity.setServiceName(rs.getString("service_name"));

                list.add(entity);
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching appointments", e);
        }
        return list;
    }
}
