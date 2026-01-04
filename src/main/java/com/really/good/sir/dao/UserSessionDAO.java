package com.really.good.sir.dao;

import com.really.good.sir.entity.UserSessionEntity;
import com.really.good.sir.entity.CredentialEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

public class UserSessionDAO {
    private static final Logger LOGGER = LogManager.getLogger(UserSessionDAO.class);

    public UserSessionEntity authorize(String email, String password) {
        EntityManager em = EntityManagerProvider.getEntityManager();

        try {
            TypedQuery<CredentialEntity> query = em.createQuery(
                    "SELECT c FROM CredentialEntity c WHERE c.email = :email",
                    CredentialEntity.class
            );
            query.setParameter("email", email);

            CredentialEntity credential;
            try {
                credential = query.getSingleResult();
            } catch (NoResultException e) {
                LOGGER.warn("No credential found for email [{}]", email);
                return null;
            }

            String inputHash = hashPassword(password);
            if (!inputHash.equals(credential.getPasswordHash())) {
                LOGGER.warn("Password mismatch for email [{}]", email);
                return null;
            }

            // Valid credential — create session
            UserSessionEntity session = new UserSessionEntity();
            session.setCredentialId(credential.getCredentialId());
            session.setLoginDateTime(new Timestamp(System.currentTimeMillis()));

            em.getTransaction().begin();
            em.persist(session);
            em.getTransaction().commit();

            // Set transient role manually
            session.setRole(credential.getRole() != null ? credential.getRole() : "UNKNOWN");

            return session;

        } catch (Exception e) {
            LOGGER.error("Authorization failed", e);
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return null;
        } finally {
            em.close();
        }
    }


    public UserSessionEntity getSessionById(int sessionId) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            UserSessionEntity session = em.find(UserSessionEntity.class, sessionId);
            if (session != null) {
                CredentialEntity credential = em.find(CredentialEntity.class, session.getCredentialId());
                session.setRole(credential != null && credential.getRole() != null
                        ? credential.getRole()
                        : "UNKNOWN");
            }
            return session;
        } catch (Exception e) {
            LOGGER.error("Error fetching session by ID {}", sessionId, e);
            return null;
        } finally {
            em.close();
        }
    }

    public boolean deleteSessionById(int sessionId) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            UserSessionEntity session = em.find(UserSessionEntity.class, sessionId);
            if (session == null) return false;

            em.getTransaction().begin();
            em.remove(session);
            em.getTransaction().commit();

            return true;
        } catch (Exception e) {
            LOGGER.error("Error deleting session with ID {}", sessionId, e);
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
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
