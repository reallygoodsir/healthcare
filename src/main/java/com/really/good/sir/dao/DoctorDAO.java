package com.really.good.sir.dao;

import com.really.good.sir.models.Doctor;
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


    public List<Doctor> getAllDoctors() {
        final List<Doctor> doctors = new ArrayList<>();
        try (final Connection connection = getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery(GET_ALL_DOCTORS)) {

            while (resultSet.next()) {
                doctors.add(mapResultSetToDoctor(resultSet));
            }
        } catch (final SQLException exception) {
            LOGGER.error("Error get all doctors", exception);
        }
        return doctors;
    }

    public Doctor getDoctorById(final int id) {
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

    public Doctor createDoctor(final Doctor doctor) {
        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(CREATE_DOCTOR,
                     Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, doctor.getFirstName());
            preparedStatement.setString(2, doctor.getLastName());
            preparedStatement.setString(3, doctor.getEmail());
            preparedStatement.setString(4, doctor.getPhone());
            preparedStatement.setInt(5, doctor.getSpecializationId());

            if (doctor.getPhoto() != null && doctor.getPhoto().length > 0) {
                preparedStatement.setBytes(6, doctor.getPhoto());
            } else {
                preparedStatement.setNull(6, Types.BLOB);
            }
            preparedStatement.executeUpdate();
            try (final ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    doctor.setId(resultSet.getInt(1));
                }
            }
        } catch (final SQLException exception) {
            LOGGER.error("Error to create doctor", exception);
        }
        return doctor;
    }

    public boolean updateDoctor(final Doctor doctor) {
        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_DOCTOR)) {
            preparedStatement.setString(1, doctor.getFirstName());
            preparedStatement.setString(2, doctor.getLastName());
            preparedStatement.setString(3, doctor.getEmail());
            preparedStatement.setString(4, doctor.getPhone());
            preparedStatement.setInt(5, doctor.getSpecializationId());

            if (doctor.getPhoto() != null && doctor.getPhoto().length > 0) {
                preparedStatement.setBytes(6, doctor.getPhoto());
            } else {
                preparedStatement.setNull(6, Types.BLOB);
            }

            preparedStatement.setInt(7, doctor.getId());
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

    private Doctor mapResultSetToDoctor(final ResultSet resultSet) throws SQLException {
        final Doctor doctor = new Doctor();
        doctor.setId(resultSet.getInt("doctor_id"));
        doctor.setFirstName(resultSet.getString("first_name"));
        doctor.setLastName(resultSet.getString("last_name"));
        doctor.setEmail(resultSet.getString("email"));
        doctor.setPhone(resultSet.getString("phone"));
        doctor.setSpecializationId(resultSet.getInt("specialization_id"));

        final Blob photoBlob = resultSet.getBlob("photo");
        if (photoBlob != null) {
            doctor.setPhoto(photoBlob.getBytes(1, (int) photoBlob.length()));
        }
        return doctor;
    }
}
