package com.really.good.sir.dao;

import com.really.good.sir.entity.SpecializationEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecializationDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(SpecializationDAO.class);
    private static final String GET_ALL_SPECIALIZATIONS = "SELECT id, name FROM specializations";
    private static final String GET_SPECIALIZATION_BY_ID = "SELECT id, name FROM specializations WHERE id = ?";

    public List<SpecializationEntity> getAllSpecializations() {
        final List<SpecializationEntity> specializationEntities = new ArrayList<>();
        try (final Connection connection = getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery(GET_ALL_SPECIALIZATIONS)) {
            while (resultSet.next()) {
                final SpecializationEntity spec = new SpecializationEntity();
                spec.setId(resultSet.getInt("id"));
                spec.setName(resultSet.getString("name"));
                specializationEntities.add(spec);
            }
        } catch (final SQLException exception) {
            LOGGER.error("Error get all specializations", exception);
        }
        return specializationEntities;
    }

    public SpecializationEntity getSpecializationById(final int id) {
        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(GET_SPECIALIZATION_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final SpecializationEntity spec = new SpecializationEntity();
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
