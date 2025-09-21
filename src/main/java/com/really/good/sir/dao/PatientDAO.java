package com.really.good.sir.dao;

import com.really.good.sir.entity.PatientEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(PatientDAO.class);

    private static final String GET_ALL_PATIENTS =
            "SELECT * FROM patients";

    private static final String GET_PATIENT_BY_ID =
            "SELECT * FROM patients WHERE patient_id = ?";

    private static final String CREATE_PATIENT =
            "INSERT INTO patients (first_name, last_name, email, phone, date_of_birth, address) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_PATIENT =
            "UPDATE patients SET first_name = ?, last_name = ?, email = ?, phone = ?, date_of_birth = ?, address = ? " +
                    "WHERE patient_id = ?";

    private static final String DELETE_PATIENT =
            "DELETE FROM patients WHERE patient_id = ?";

    public List<PatientEntity> getAllPatients() {
        final List<PatientEntity> patientEntities = new ArrayList<>();
        try (final Connection connection = getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet rs = statement.executeQuery(GET_ALL_PATIENTS)) {

            while (rs.next()) {
                patientEntities.add(mapResultSetToPatient(rs));
            }
        } catch (final SQLException e) {
            LOGGER.error("Error getting all patients", e);
        }
        return patientEntities;
    }

    public PatientEntity getPatientById(final int id) {
        try (final Connection connection = getConnection();
             final PreparedStatement ps = connection.prepareStatement(GET_PATIENT_BY_ID)) {
            LOGGER.info("ID: {}", id);
            ps.setInt(1, id);
            try (final ResultSet rs = ps.executeQuery()) {
                LOGGER.info("RS: {}", rs);
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("Error getting patient by id {}", id, exception);
        }
        return null;
    }

    public PatientEntity createPatient(final PatientEntity patientEntity) {
        try (final Connection connection = getConnection();
             final PreparedStatement ps = connection.prepareStatement(CREATE_PATIENT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, patientEntity.getFirstName());
            ps.setString(2, patientEntity.getLastName());
            ps.setString(3, patientEntity.getEmail());
            ps.setString(4, patientEntity.getPhone());
            ps.setDate(5, patientEntity.getDateOfBirth());
            ps.setString(6, patientEntity.getAddress());

            ps.executeUpdate();

            try (final ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    patientEntity.setId(rs.getInt(1));
                }
            }
        } catch (final SQLException exception) {
            LOGGER.error("Error creating patient", exception);
        }
        return patientEntity;
    }

    public boolean updatePatient(final PatientEntity patientEntity) {
        try (final Connection connection = getConnection();
             final PreparedStatement ps = connection.prepareStatement(UPDATE_PATIENT)) {
            ps.setString(1, patientEntity.getFirstName());
            ps.setString(2, patientEntity.getLastName());
            ps.setString(3, patientEntity.getEmail());
            ps.setString(4, patientEntity.getPhone());
            ps.setDate(5, patientEntity.getDateOfBirth());
            ps.setString(6, patientEntity.getAddress());
            ps.setInt(7, patientEntity.getId());

            final int updated = ps.executeUpdate();
            return updated > 0;
        } catch (final SQLException exception) {
            LOGGER.error("Error updating patient", exception);
        }
        return false;
    }

    public boolean deletePatient(final int id) {
        try (final Connection connection = getConnection();
             final PreparedStatement ps = connection.prepareStatement(DELETE_PATIENT)) {
            ps.setInt(1, id);

            int deleted = ps.executeUpdate();
            return deleted > 0;
        } catch (final SQLException exception) {
            LOGGER.error("Error deleting patient", exception);
        }
        return false;
    }

    private PatientEntity mapResultSetToPatient(final ResultSet rs) throws SQLException {
        final PatientEntity patientEntity = new PatientEntity();
        patientEntity.setId(rs.getInt("patient_id"));
        patientEntity.setFirstName(rs.getString("first_name"));
        patientEntity.setLastName(rs.getString("last_name"));
        patientEntity.setEmail(rs.getString("email"));
        patientEntity.setPhone(rs.getString("phone"));
        patientEntity.setDateOfBirth(rs.getDate("date_of_birth"));
        patientEntity.setAddress(rs.getString("address"));
        LOGGER.info(patientEntity);
        return patientEntity;
    }
}
