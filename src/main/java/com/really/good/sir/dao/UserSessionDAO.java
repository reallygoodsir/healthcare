package com.really.good.sir.dao;

import com.really.good.sir.entity.UserSessionEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class UserSessionDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(UserSessionDAO.class);

    // now selects role as well
    private static final String GET_CREDENTIAL =
            "SELECT credential_id, password_hash, role FROM credentials WHERE email = ?";

    private static final String CREATE_SESSION =
            "INSERT INTO user_sessions (credential_id, login_date_time) VALUES (?, NOW())";

    // Updated to join credentials to get role
    private static final String GET_SESSION =
            "SELECT us.id, us.credential_id, us.login_date_time, c.role " +
                    "FROM user_sessions us " +
                    "JOIN credentials c ON us.credential_id = c.credential_id " +
                    "WHERE us.id = ?";

    private static final String DELETE_SESSION =
            "DELETE FROM user_sessions WHERE id = ?";

    public UserSessionEntity authorize(String email, String password) {
        try (Connection conn = getConnection()) {

            Integer credentialId = null;
            String role = null;
            try (PreparedStatement ps = conn.prepareStatement(GET_CREDENTIAL)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedHash = rs.getString("password_hash");
                        String inputHash = hashPassword(password);
                        if (storedHash != null && storedHash.equals(inputHash)) {
                            credentialId = rs.getInt("credential_id");
                            role = rs.getString("role");
                        } else {
                            LOGGER.warn("Password mismatch for email [{}]", email);
                        }
                    } else {
                        LOGGER.warn("No credential found for email [{}]", email);
                    }
                }
            }

            if (credentialId == null) {
                return null;
            }

            try (PreparedStatement ps = conn.prepareStatement(CREATE_SESSION, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, credentialId);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        UserSessionEntity session = new UserSessionEntity();
                        session.setId(rs.getInt(1));
                        session.setCredentialId(credentialId);
                        session.setLoginDateTime(new Timestamp(System.currentTimeMillis()));
                        session.setRole(role != null ? role : "UNKNOWN");
                        return session;
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("Authorization failed", e);
        }
        return null;
    }

    public UserSessionEntity getSessionById(int sessionId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_SESSION)) {

            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserSessionEntity session = new UserSessionEntity();
                    session.setId(rs.getInt("id"));
                    session.setCredentialId(rs.getInt("credential_id"));
                    session.setLoginDateTime(rs.getTimestamp("login_date_time"));
                    session.setRole(rs.getString("role")); // <-- now includes role
                    return session;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching session by ID {}", sessionId, e);
        }
        return null;
    }

    public boolean deleteSessionById(int sessionId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SESSION)) {

            ps.setInt(1, sessionId);
            int affected = ps.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            LOGGER.error("Error deleting session with ID {}", sessionId, e);
            return false;
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
