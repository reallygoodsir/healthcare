package com.really.good.sir.dao;

import com.really.good.sir.entity.ServiceEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(ServiceDAO.class);

    private static final String GET_ALL_SERVICES = "SELECT * FROM service";
    private static final String GET_SERVICE_BY_ID = "SELECT * FROM service WHERE id = ?";
    private static final String CREATE_SERVICE = "INSERT INTO service (name, price) VALUES (?, ?)";
    private static final String UPDATE_SERVICE = "UPDATE service SET name = ?, price = ? WHERE id = ?";
    private static final String DELETE_SERVICE = "DELETE FROM service WHERE id = ?";
    private static final String CHECK_SERVICE_NAME_EXISTS_EXCLUDE_ID = "SELECT COUNT(*) FROM service WHERE name = ? AND id <> ?";

    public List<ServiceEntity> getAllServices() {
        List<ServiceEntity> services = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_SERVICES)) {

            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching all services", e);
        }
        return services;
    }

    public ServiceEntity getServiceById(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_SERVICE_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToService(rs);
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching service by id {}", id, e);
        }
        return null;
    }

    public ServiceEntity createService(ServiceEntity service) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(CREATE_SERVICE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, service.getName());
            ps.setInt(2, service.getPrice());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) service.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            LOGGER.error("Error creating service", e);
        }
        return service;
    }

    public boolean isServiceNameExists(String name, int excludeId) {
        String sql = excludeId > 0
                ? "SELECT COUNT(*) FROM service WHERE name = ? AND id <> ?"
                : "SELECT COUNT(*) FROM service WHERE name = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            if (excludeId > 0) ps.setInt(2, excludeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Error checking service name existence", e);
        }
        return false;
    }


    public boolean updateService(ServiceEntity service) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SERVICE)) {
            ps.setString(1, service.getName());
            ps.setInt(2, service.getPrice());
            ps.setInt(3, service.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error updating service", e);
        }
        return false;
    }

    public boolean deleteService(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SERVICE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error deleting service", e);
        }
        return false;
    }

    private ServiceEntity mapResultSetToService(ResultSet rs) throws SQLException {
        ServiceEntity service = new ServiceEntity();
        service.setId(rs.getInt("id"));
        service.setName(rs.getString("name"));
        service.setPrice(rs.getInt("price"));
        LOGGER.info(service);
        return service;
    }
}
