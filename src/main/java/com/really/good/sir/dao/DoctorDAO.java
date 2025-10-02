package com.really.good.sir.dao;

import com.really.good.sir.entity.DoctorEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(DoctorDAO.class);
    private static final String GET_ALL_DOCTORS =
            "SELECT d.*, s.name AS specialization_name " +
                    "FROM doctors d " +
                    "LEFT JOIN specializations s ON d.specialization_id = s.id";

    private static final String GET_DOCTOR_BY_ID =
            "SELECT d.*, s.name AS specialization_name " +
                    "FROM doctors d " +
                    "LEFT JOIN specializations s ON d.specialization_id = s.id " +
                    "WHERE d.doctor_id = ?";

    private static final String CREATE_DOCTOR =
            "INSERT INTO doctors (first_name, last_name, email, phone, specialization_id, photo) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_DOCTOR =
            "UPDATE doctors " +
                    "SET first_name = ?, " +
                    "last_name = ?, " +
                    "email = ?, " +
                    "phone = ?, " +
                    "specialization_id = ?, " +
                    "photo = ? " +
                    "WHERE doctor_id = ?";

    private static final String DELETE_DOCTOR =
            "DELETE FROM doctors " +
                    "WHERE doctor_id = ?";

    private static final String GET_DOCTORS_BY_SERVICE =
            "SELECT d.doctor_id, d.first_name, d.last_name, d.email, d.phone, d.specialization_id, d.photo " +
                    "FROM doctors d " +
                    "WHERE d.specialization_id IN ( " +
                    "    SELECT ss.specialization_id FROM `service-specializations` ss WHERE ss.service_id = ? " +
                    ")";

    public List<DoctorEntity> getAllDoctors() {
        final List<DoctorEntity> doctorEntities = new ArrayList<>();
        try (final Connection connection = getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery(GET_ALL_DOCTORS)) {

            while (resultSet.next()) {
                doctorEntities.add(mapResultSetToDoctor(resultSet));
            }
        } catch (final SQLException exception) {
            LOGGER.error("Error get all doctors", exception);
        }
        return doctorEntities;
    }

    public DoctorEntity getDoctorById(final int id) {
        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(GET_DOCTOR_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToDoctor(resultSet);
                }
            }
        } catch (final SQLException exception) {
            LOGGER.error("Error get doctor by id {}", id, exception);
        }
        return null;
    }

    public List<DoctorEntity> getDoctorsByServiceId(final int serviceId) {
        final List<DoctorEntity> doctorEntities = new ArrayList<>();
        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(GET_DOCTORS_BY_SERVICE)) {

            preparedStatement.setInt(1, serviceId);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    doctorEntities.add(mapResultSetToDoctor(resultSet));
                }
            }
        } catch (final SQLException exception) {
            LOGGER.error("Error getting doctors for serviceId {}", serviceId, exception);
        }
        return doctorEntities;
    }

    public DoctorEntity createDoctor(final DoctorEntity doctorEntity) {
        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(CREATE_DOCTOR,
                     Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, doctorEntity.getFirstName());
            preparedStatement.setString(2, doctorEntity.getLastName());
            preparedStatement.setString(3, doctorEntity.getEmail());
            preparedStatement.setString(4, doctorEntity.getPhone());
            preparedStatement.setInt(5, doctorEntity.getSpecializationId());

            if (doctorEntity.getPhoto() != null && doctorEntity.getPhoto().length > 0) {
                preparedStatement.setBytes(6, doctorEntity.getPhoto());
            } else {
                preparedStatement.setNull(6, Types.BLOB);
            }
            preparedStatement.executeUpdate();
            try (final ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    doctorEntity.setId(resultSet.getInt(1));
                }
            }
        } catch (final SQLException exception) {
            LOGGER.error("Error to create doctor", exception);
        }
        return doctorEntity;
    }

    public boolean updateDoctor(final DoctorEntity doctorEntity) {
        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_DOCTOR)) {
            preparedStatement.setString(1, doctorEntity.getFirstName());
            preparedStatement.setString(2, doctorEntity.getLastName());
            preparedStatement.setString(3, doctorEntity.getEmail());
            preparedStatement.setString(4, doctorEntity.getPhone());
            preparedStatement.setInt(5, doctorEntity.getSpecializationId());

            if (doctorEntity.getPhoto() != null && doctorEntity.getPhoto().length > 0) {
                preparedStatement.setBytes(6, doctorEntity.getPhoto());
            } else {
                preparedStatement.setNull(6, Types.BLOB);
            }

            preparedStatement.setInt(7, doctorEntity.getId());
            final int updated = preparedStatement.executeUpdate();
            return updated > 0;
        } catch (final SQLException exception) {
            LOGGER.error("Error to update doctor", exception);
        }
        return false;
    }

    public boolean deleteDoctor(final int id) {
        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_DOCTOR)) {
            preparedStatement.setInt(1, id);
            final int deleted = preparedStatement.executeUpdate();
            return deleted > 0;
        } catch (final SQLException exception) {
            LOGGER.error("Error to delete doctor", exception);
        }
        return false;
    }

    private DoctorEntity mapResultSetToDoctor(final ResultSet resultSet) throws SQLException {
        final DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setId(resultSet.getInt("doctor_id"));
        doctorEntity.setFirstName(resultSet.getString("first_name"));
        doctorEntity.setLastName(resultSet.getString("last_name"));
        doctorEntity.setEmail(resultSet.getString("email"));
        doctorEntity.setPhone(resultSet.getString("phone"));
        doctorEntity.setSpecializationId(resultSet.getInt("specialization_id"));

        final Blob photoBlob = resultSet.getBlob("photo");
        if (photoBlob != null) {
            doctorEntity.setPhoto(photoBlob.getBytes(1, (int) photoBlob.length()));
        }
        return doctorEntity;
    }
}
