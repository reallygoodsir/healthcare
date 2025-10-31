package com.really.good.sir.dao;

import com.really.good.sir.dto.PatientAppointmentDetailsDTO;
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

    private static final String GET_APPOINTMENTS_BY_DOCTOR =
            "SELECT pa.*, p.first_name AS patient_first_name, p.last_name AS patient_last_name, s.name AS service_name " +
                    "FROM patient_appointments pa " +
                    "JOIN patients p ON pa.patient_id = p.patient_id " +
                    "JOIN service s ON pa.service_id = s.id " +
                    "WHERE pa.doctor_id = ?";

    private static final String GET_TODAYS_APPOINTMENTS_BY_DOCTOR =
            "SELECT pa.*, p.first_name AS patient_first_name, p.last_name AS patient_last_name, s.name AS service_name " +
                    "FROM patient_appointments pa " +
                    "JOIN patients p ON pa.patient_id = p.patient_id " +
                    "JOIN service s ON pa.service_id = s.id " +
                    "WHERE pa.doctor_id = ? AND pa.date = CURRENT_DATE";

    private static final String UPDATE_STATUS =
            "UPDATE patient_appointments SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE appointment_id = ?";

    private static final String GET_STATUS_BY_APPOINTMENT_ID =
            "SELECT status FROM patient_appointments WHERE appointment_id = ?";

    private static final String CHECK_OVERLAPPING_APPOINTMENTS = "SELECT COUNT(*) FROM patient_appointments " +
            "WHERE doctor_id = ? AND date = ? " +
            "AND ((start_time < ? AND end_time > ?) OR " +
            "     (start_time >= ? AND start_time < ?))";

    private static final String GET_APPOINTMENT_DETAILS_BY_PATIENT =
            "SELECT pa.appointment_id, pa.date, pa.start_time, pa.end_time, pa.status, " +
                    "       d.doctor_id, d.first_name AS doctor_first_name, d.last_name AS doctor_last_name, " +
                    "       s.id AS service_id, s.name AS service_name " +
                    "FROM patient_appointments pa " +
                    "JOIN doctors d ON pa.doctor_id = d.doctor_id " +
                    "JOIN service s ON pa.service_id = s.id " +
                    "WHERE pa.patient_id = ? " +
                    "ORDER BY pa.date DESC, pa.start_time DESC";



    public List<PatientAppointmentEntity> getTodaysAppointmentsByDoctor(int doctorId) {
        List<PatientAppointmentEntity> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_TODAYS_APPOINTMENTS_BY_DOCTOR)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEntity(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching today's appointments by doctor", e);
        }
        return list;
    }

    public List<PatientAppointmentDetailsDTO> getAppointmentDetailsByPatientId(int patientId) {
        List<PatientAppointmentDetailsDTO> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_APPOINTMENT_DETAILS_BY_PATIENT)) {
            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PatientAppointmentDetailsDTO dto = new PatientAppointmentDetailsDTO();
                    dto.setAppointmentId(rs.getInt("appointment_id"));
                    dto.setDate(rs.getString("date"));
                    dto.setStartTime(rs.getString("start_time"));
                    dto.setEndTime(rs.getString("end_time"));
                    dto.setStatus(rs.getString("status"));
                    dto.setDoctorId(rs.getInt("doctor_id"));
                    dto.setDoctorFirstName(rs.getString("doctor_first_name"));
                    dto.setDoctorLastName(rs.getString("doctor_last_name"));
                    dto.setServiceId(rs.getInt("service_id"));
                    dto.setServiceName(rs.getString("service_name"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching appointment details by patient id {}", patientId, e);
        }

        return list;
    }



    public boolean updateStatus(int appointmentId, String status) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to update appointment status", e);
            return false;
        }
    }

    public PatientAppointmentEntity createAppointment(PatientAppointmentEntity entity) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(CREATE_APPOINTMENT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entity.getPatientId());
            ps.setInt(2, entity.getServiceId());
            ps.setInt(3, entity.getDoctorId());
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
                list.add(mapEntity(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching appointments", e);
        }
        return list;
    }

    public List<PatientAppointmentEntity> getAppointmentsByDoctorId(int doctorId) {
        List<PatientAppointmentEntity> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_APPOINTMENTS_BY_DOCTOR)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEntity(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching appointments by doctor", e);
        }
        return list;
    }

    public String getAppointmentStatusById(int appointmentId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_STATUS_BY_APPOINTMENT_ID)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching appointment status", e);
        }
        return null;
    }

    public boolean hasOverlappingAppointment(int doctorId, Date date, Time startTime, Time endTime) {

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_OVERLAPPING_APPOINTMENTS)) {

            ps.setInt(1, doctorId);
            ps.setDate(2, date);
            ps.setTime(3, endTime);
            ps.setTime(4, startTime);
            ps.setTime(5, startTime);
            ps.setTime(6, endTime);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error checking overlapping appointments for doctorId=" + doctorId, e);
        }
        return false;
    }

    
    private PatientAppointmentEntity mapEntity(ResultSet rs) throws SQLException {
        PatientAppointmentEntity entity = new PatientAppointmentEntity();
        entity.setAppointmentId(rs.getInt("appointment_id"));
        entity.setPatientId(rs.getInt("patient_id"));
        entity.setServiceId(rs.getInt("service_id"));
        entity.setDoctorId(rs.getInt("doctor_id"));
        entity.setDate(rs.getDate("date"));
        entity.setStartTime(rs.getTime("start_time"));
        entity.setEndTime(rs.getTime("end_time"));
        entity.setStatus(rs.getString("status"));
        entity.setPatientFirstName(rs.getString("patient_first_name"));
        entity.setPatientLastName(rs.getString("patient_last_name"));
        entity.setServiceName(rs.getString("service_name"));
        return entity;
    }

}
