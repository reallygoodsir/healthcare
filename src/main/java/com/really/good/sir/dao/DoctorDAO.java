package com.really.good.sir.dao;

import com.really.good.sir.entity.DoctorEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DoctorDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(DoctorDAO.class);

    private static final String GET_ALL_DOCTORS =
            "SELECT d.doctor_id, d.first_name, d.last_name, d.specialization_id, d.photo, " +
                    "c.email, c.phone " +
                    "FROM doctors d " +
                    "LEFT JOIN credentials c ON d.credential_id = c.credential_id";

    private static final String GET_DOCTOR_BY_ID =
            "SELECT d.doctor_id, d.first_name, d.last_name, d.specialization_id, d.photo, " +
                    "c.email, c.phone " +
                    "FROM doctors d " +
                    "LEFT JOIN credentials c ON d.credential_id = c.credential_id " +
                    "WHERE d.doctor_id = ?";

    private static final String GET_DOCTORS_BY_SERVICE =
            "SELECT d.doctor_id, d.first_name, d.last_name, d.specialization_id, d.photo, " +
                    "c.email, c.phone " +
                    "FROM doctors d " +
                    "LEFT JOIN credentials c ON d.credential_id = c.credential_id " +
                    "WHERE d.specialization_id IN (SELECT ss.specialization_id FROM `service-specializations` ss WHERE ss.service_id = ?)";

    private static final String CREATE_CREDENTIAL =
            "INSERT INTO credentials (email, phone, password_hash, role) VALUES (?, ?, ?, 'DOCTOR')";

    private static final String CREATE_DOCTOR =
            "INSERT INTO doctors (first_name, last_name, specialization_id, credential_id, photo, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, NOW())";

    private static final String UPDATE_DOCTOR =
            "UPDATE doctors SET first_name = ?, last_name = ?, specialization_id = ?, photo = ? WHERE doctor_id = ?";

    private static final String UPDATE_CREDENTIAL =
            "UPDATE credentials SET email = ?, phone = ? WHERE credential_id = ?";

    private static final String DELETE_DOCTOR =
            "DELETE FROM doctors WHERE doctor_id = ?";

    private static final String GET_CREDENTIAL_ID =
            "SELECT credential_id FROM doctors WHERE doctor_id = ?";

    private static final String GET_DOCTOR_ID_BY_CREDENTIAL =
            "SELECT doctor_id FROM doctors WHERE credential_id = ?";

    // --- CREATE DOCTOR WITH CREDENTIAL ---
    public DoctorEntity createDoctor(final DoctorEntity doctorEntity) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false); // start transaction

            String rawPassword = generateRandomPassword();
            LOGGER.info("Password: {}", rawPassword);
            String passwordHash = hashPassword(rawPassword);

            int credentialId;
            try (PreparedStatement psCredential = connection.prepareStatement(CREATE_CREDENTIAL, Statement.RETURN_GENERATED_KEYS)) {
                psCredential.setString(1, doctorEntity.getEmail());
                psCredential.setString(2, doctorEntity.getPhone());
                psCredential.setString(3, passwordHash);
                psCredential.executeUpdate();

                try (ResultSet rs = psCredential.getGeneratedKeys()) {
                    if (rs.next()) {
                        credentialId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to generate credential_id");
                    }
                }
            }

            try (PreparedStatement psDoctor = connection.prepareStatement(CREATE_DOCTOR, Statement.RETURN_GENERATED_KEYS)) {
                psDoctor.setString(1, doctorEntity.getFirstName());
                psDoctor.setString(2, doctorEntity.getLastName());
                psDoctor.setInt(3, doctorEntity.getSpecializationId());
                psDoctor.setInt(4, credentialId);

                if (doctorEntity.getPhoto() != null && doctorEntity.getPhoto().length > 0) {
                    psDoctor.setBytes(5, doctorEntity.getPhoto());
                } else {
                    psDoctor.setNull(5, Types.BLOB);
                }

                psDoctor.executeUpdate();

                try (ResultSet rs = psDoctor.getGeneratedKeys()) {
                    if (rs.next()) {
                        doctorEntity.setId(rs.getInt(1));
                    }
                }
            }

            connection.commit();
            return doctorEntity;

        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    LOGGER.error("Rollback failed", ex);
                }
            }
            LOGGER.error("Error creating doctor", e);
            return null;
        } finally {
            closeConnection(connection);
        }
    }

    // --- GET ALL DOCTORS ---
    public List<DoctorEntity> getAllDoctors() {
        List<DoctorEntity> doctors = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_DOCTORS)) {

            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching all doctors", e);
        }
        return doctors;
    }

    // --- GET DOCTOR BY ID ---
    public DoctorEntity getDoctorById(final int doctorId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_DOCTOR_BY_ID)) {

            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDoctor(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching doctor by ID {}", doctorId, e);
        }
        return null;
    }

    // --- GET DOCTORS BY SERVICE ---
    public List<DoctorEntity> getDoctorsByServiceId(final int serviceId) {
        List<DoctorEntity> doctors = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_DOCTORS_BY_SERVICE)) {

            ps.setInt(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching doctors by service ID {}", serviceId, e);
        }
        return doctors;
    }

    // --- UPDATE DOCTOR AND CREDENTIAL ---
    public boolean updateDoctor(final DoctorEntity doctorEntity) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement psDoctor = connection.prepareStatement(UPDATE_DOCTOR)) {
                psDoctor.setString(1, doctorEntity.getFirstName());
                psDoctor.setString(2, doctorEntity.getLastName());
                psDoctor.setInt(3, doctorEntity.getSpecializationId());

                if (doctorEntity.getPhoto() != null && doctorEntity.getPhoto().length > 0) {
                    psDoctor.setBytes(4, doctorEntity.getPhoto());
                } else {
                    psDoctor.setNull(4, Types.BLOB);
                }
                psDoctor.setInt(5, doctorEntity.getId());
                psDoctor.executeUpdate();
            }

            int credentialId = getCredentialId(connection, doctorEntity.getId());
            if (credentialId > 0) {
                try (PreparedStatement psCredential = connection.prepareStatement(UPDATE_CREDENTIAL)) {
                    psCredential.setString(1, doctorEntity.getEmail());
                    psCredential.setString(2, doctorEntity.getPhone());
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
            LOGGER.error("Error updating doctor", e);
            return false;
        } finally {
            closeConnection(connection);
        }
    }

    // --- DELETE DOCTOR ---
    public boolean deleteDoctor(final int doctorId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_DOCTOR)) {

            ps.setInt(1, doctorId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Error deleting doctor {}", doctorId, e);
            return false;
        }
    }

    // --- NEW: GET DOCTOR ID BY CREDENTIAL ID ---
    public int getDoctorIdByCredentialId(final int credentialId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_DOCTOR_ID_BY_CREDENTIAL)) {

            ps.setInt(1, credentialId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("doctor_id");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching doctor ID by credential ID {}", credentialId, e);
        }
        return -1;
    }

    // --- UTILS ---
    private DoctorEntity mapResultSetToDoctor(ResultSet rs) throws SQLException {
        DoctorEntity doctor = new DoctorEntity();
        doctor.setId(rs.getInt("doctor_id"));
        doctor.setFirstName(rs.getString("first_name"));
        doctor.setLastName(rs.getString("last_name"));
        doctor.setSpecializationId(rs.getInt("specialization_id"));
        doctor.setPhoto(rs.getBytes("photo"));
        doctor.setEmail(rs.getString("email"));
        doctor.setPhone(rs.getString("phone"));
        return doctor;
    }

    private int getCredentialId(Connection conn, int doctorId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(GET_CREDENTIAL_ID)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("credential_id");
                }
            }
        }
        return -1;
    }

    private String generateRandomPassword() {
        Random random = new Random();
        int number = 10000000 + random.nextInt(90000000);
        return String.valueOf(number);
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
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
