package com.really.good.sir.dao;

import com.really.good.sir.entity.PatientEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PatientDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(PatientDAO.class);

    private static final String GET_ALL_PATIENTS =
            "SELECT p.patient_id, p.first_name, p.last_name, p.date_of_birth, p.address, " +
                    "c.email, c.phone " +
                    "FROM patients p " +
                    "LEFT JOIN credentials c ON p.credential_id = c.credential_id";

    private static final String GET_PATIENT_BY_ID =
            "SELECT p.patient_id, p.first_name, p.last_name, p.date_of_birth, p.address, " +
                    "c.email, c.phone " +
                    "FROM patients p " +
                    "LEFT JOIN credentials c ON p.credential_id = c.credential_id " +
                    "WHERE p.patient_id = ?";

    private static final String GET_PATIENT_BY_PHONE =
            "SELECT p.patient_id, p.first_name, p.last_name, p.date_of_birth, p.address, " +
                    "c.email, c.phone " +
                    "FROM patients p " +
                    "LEFT JOIN credentials c ON p.credential_id = c.credential_id " +
                    "WHERE c.phone = ?";

    // --- Updated: now includes role column ---
    private static final String CREATE_CREDENTIAL =
            "INSERT INTO credentials (email, phone, password_hash, role) VALUES (?, ?, ?, ?)";

    private static final String CREATE_PATIENT =
            "INSERT INTO patients (first_name, last_name, date_of_birth, address, credential_id) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_PATIENT =
            "UPDATE patients SET first_name = ?, last_name = ?, date_of_birth = ?, address = ? WHERE patient_id = ?";

    private static final String UPDATE_CREDENTIAL =
            "UPDATE credentials SET email = ?, phone = ? WHERE credential_id = ?";

    private static final String DELETE_PATIENT =
            "DELETE FROM patients WHERE patient_id = ?";

    private static final String GET_CREDENTIAL_ID =
            "SELECT credential_id FROM patients WHERE patient_id = ?";

    // --- CREATE PATIENT WITH CREDENTIAL ---
    public PatientEntity createPatient(PatientEntity patientEntity) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            // Generate random 8-digit password
            String rawPassword = generateRandomPassword();
            LOGGER.info("Patient password: {}", rawPassword);
            String passwordHash = hashPassword(rawPassword);

            // Insert credential (role = PATIENT)
            int credentialId;
            try (PreparedStatement psCredential = connection.prepareStatement(CREATE_CREDENTIAL, Statement.RETURN_GENERATED_KEYS)) {
                psCredential.setString(1, patientEntity.getEmail());
                psCredential.setString(2, patientEntity.getPhone());
                psCredential.setString(3, passwordHash);
                psCredential.setString(4, "PATIENT"); // 👈 assigned role
                psCredential.executeUpdate();

                try (ResultSet rs = psCredential.getGeneratedKeys()) {
                    if (rs.next()) {
                        credentialId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to generate credential_id");
                    }
                }
            }

            // Insert patient
            try (PreparedStatement psPatient = connection.prepareStatement(CREATE_PATIENT, Statement.RETURN_GENERATED_KEYS)) {
                psPatient.setString(1, patientEntity.getFirstName());
                psPatient.setString(2, patientEntity.getLastName());
                psPatient.setDate(3, patientEntity.getDateOfBirth());
                psPatient.setString(4, patientEntity.getAddress());
                psPatient.setInt(5, credentialId);
                psPatient.executeUpdate();

                try (ResultSet rs = psPatient.getGeneratedKeys()) {
                    if (rs.next()) {
                        patientEntity.setId(rs.getInt(1));
                    }
                }
            }

            connection.commit();

            return patientEntity;

        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    LOGGER.error("Rollback failed", ex);
                }
            }
            LOGGER.error("Error creating patient", e);
            return null;
        } finally {
            closeConnection(connection);
        }
    }

    // --- GET ALL PATIENTS ---
    public List<PatientEntity> getAllPatients() {
        List<PatientEntity> patients = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_PATIENTS)) {

            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting all patients", e);
        }
        return patients;
    }

    // --- GET PATIENT BY ID ---
    public PatientEntity getPatientById(int patientId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_PATIENT_BY_ID)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting patient by ID {}", patientId, e);
        }
        return null;
    }

    // --- GET PATIENT BY PHONE ---
    public PatientEntity getPatientByPhone(String phoneNumber) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_PATIENT_BY_PHONE)) {

            ps.setString(1, phoneNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting patient by phone {}", phoneNumber, e);
        }
        return null;
    }

    // --- UPDATE PATIENT AND CREDENTIAL ---
    public boolean updatePatient(PatientEntity patientEntity) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            // Update patient
            try (PreparedStatement psPatient = connection.prepareStatement(UPDATE_PATIENT)) {
                psPatient.setString(1, patientEntity.getFirstName());
                psPatient.setString(2, patientEntity.getLastName());
                psPatient.setDate(3, patientEntity.getDateOfBirth());
                psPatient.setString(4, patientEntity.getAddress());
                psPatient.setInt(5, patientEntity.getId());
                psPatient.executeUpdate();
            }

            // Update credentials
            int credentialId = getCredentialId(connection, patientEntity.getId());
            if (credentialId > 0) {
                try (PreparedStatement psCredential = connection.prepareStatement(UPDATE_CREDENTIAL)) {
                    psCredential.setString(1, patientEntity.getEmail());
                    psCredential.setString(2, patientEntity.getPhone());
                    psCredential.setInt(3, credentialId);
                    psCredential.executeUpdate();
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    LOGGER.error("Rollback failed", ex);
                }
            }
            LOGGER.error("Error updating patient", e);
            return false;
        } finally {
            closeConnection(connection);
        }
    }

    // --- DELETE PATIENT ---
    public boolean deletePatient(int patientId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_PATIENT)) {

            ps.setInt(1, patientId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Error deleting patient {}", patientId, e);
            return false;
        }
    }

    // --- UTILS ---
    private PatientEntity mapResultSetToPatient(ResultSet rs) throws SQLException {
        PatientEntity patient = new PatientEntity();
        patient.setId(rs.getInt("patient_id"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setDateOfBirth(rs.getDate("date_of_birth"));
        patient.setAddress(rs.getString("address"));
        patient.setEmail(rs.getString("email"));
        patient.setPhone(rs.getString("phone"));
        return patient;
    }

    private int getCredentialId(Connection conn, int patientId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(GET_CREDENTIAL_ID)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("credential_id");
            }
        }
        return -1;
    }

    private String generateRandomPassword() {
        Random random = new Random();
        int number = 10000000 + random.nextInt(90000000); // 8-digit
        return String.valueOf(number);
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("Failed to close connection", e);
            }
        }
    }
}
