package com.really.good.sir.dao;

import com.really.good.sir.models.Specialization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecializationDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(SpecializationDAO.class);
    private static final String GET_ALL_SPECIALIZATIONS = "SELECT id, name FROM specializations";
    private static final String GET_SPECIALIZATION_BY_ID = "SELECT id, name FROM specializations WHERE id = ?";

    public List<Specialization> getAllSpecializations() {
        final List<Specialization> specializations = new ArrayList<>();
        try (final Connection connection = getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery(GET_ALL_SPECIALIZATIONS)) {
            while (resultSet.next()) {
                final Specialization spec = new Specialization();
                spec.setId(resultSet.getInt("id"));
                spec.setName(resultSet.getString("name"));
                specializations.add(spec);
            }
        } catch (final SQLException exception) {
            LOGGER.error("Error get all specializations", exception);
        }
        return specializations;
    }

    public Specialization getSpecializationById(final int id) {
        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(GET_SPECIALIZATION_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final Specialization spec = new Specialization();
                    spec.setId(resultSet.getInt("id"));
                    spec.setName(resultSet.getString("name"));
                    return spec;
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("Error get specialization by id {}", id, exception);
        }
        return null;
    }
}
