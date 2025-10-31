package com.really.good.sir.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CredentialDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(CredentialDAO.class);

    private static final String GET_ID_BY_EMAIL =
            "SELECT credential_id FROM credentials WHERE email = ?";
    private static final String GET_ID_BY_PHONE =
            "SELECT credential_id FROM credentials WHERE phone = ?";
    private static final String VERIFY_EMAIL =
            "SELECT email FROM credentials WHERE email = ?";
    private static final String VERIFY_PHONE =
            "SELECT phone FROM credentials WHERE phone = ?";

    public int getCredentialIdByEmail(String email) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_ID_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("credential_id");
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Failed to get credential id by email", exception);
        }
        return -1;
    }

    public int getCredentialIdByPhone(String phone) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_ID_BY_PHONE)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("credential_id");
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Failed to get credential id by phone", exception);
        }
        return -1;
    }

    public boolean isEmailUnique(String email) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(VERIFY_EMAIL)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    return !rs.next();
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Email verification failed", exception);
        }
        return false;
    }

    public boolean isPhoneUnique(String phone) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(VERIFY_PHONE)) {
                ps.setString(1, phone);
                try (ResultSet rs = ps.executeQuery()) {
                    return !rs.next();
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Phone verification failed", exception);
        }
        return false;
    }
}
