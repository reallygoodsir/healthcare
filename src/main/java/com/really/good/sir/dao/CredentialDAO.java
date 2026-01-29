package com.really.good.sir.dao;

import com.really.good.sir.config.EntityManagerConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;

public class CredentialDAO {
    private static final Logger LOGGER = LogManager.getLogger(CredentialDAO.class);

    private static final String GET_ID_BY_EMAIL =
            "SELECT credential_id FROM credentials WHERE email = ?";

    private static final String GET_ID_BY_PHONE =
            "SELECT credential_id FROM credentials WHERE phone = ?";

    private static final String GET_ID_BY_ID =
            "SELECT credential_id FROM credentials WHERE credential_id = ?";

    private static final String VERIFY_EMAIL =
            "SELECT 1 FROM credentials WHERE email = ?";

    private static final String VERIFY_PHONE =
            "SELECT 1 FROM credentials WHERE phone = ?";

    public int getCredentialIdByEmail(String email) {
        EntityManager em = null;
        try {
            em = EntityManagerConfiguration.getEntityManager();
            Object result = em.createNativeQuery(GET_ID_BY_EMAIL)
                    .setParameter(1, email)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            return result != null ? ((Number) result).intValue() : -1;
        } catch (Exception e) {
            LOGGER.error("Failed to get credential id by email", e);
            return -1;
        } finally {
            if (em != null) em.close();
        }
    }

    public int getCredentialIdByPhone(String phone) {
        EntityManager em = null;
        try {
            em = EntityManagerConfiguration.getEntityManager();
            Object result = em.createNativeQuery(GET_ID_BY_PHONE)
                    .setParameter(1, phone)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            return result != null ? ((Number) result).intValue() : -1;
        } catch (Exception e) {
            LOGGER.error("Failed to get credential id by phone", e);
            return -1;
        } finally {
            if (em != null) em.close();
        }
    }

    public int getCredentialIdById(Integer id) {
        EntityManager em = null;
        try {
            em = EntityManagerConfiguration.getEntityManager();
            Object result = em.createNativeQuery(GET_ID_BY_ID)
                    .setParameter(1, id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            return result != null ? ((Number) result).intValue() : -1;
        } catch (Exception e) {
            LOGGER.error("Failed to get credential id by id", e);
            return -1;
        } finally {
            if (em != null) em.close();
        }
    }

    public boolean isEmailUnique(String email) {
        EntityManager em = null;
        try {
            em = EntityManagerConfiguration.getEntityManager();
            return em.createNativeQuery(VERIFY_EMAIL)
                    .setParameter(1, email)
                    .getResultList()
                    .isEmpty();
        } catch (Exception e) {
            LOGGER.error("Email verification failed", e);
            return false;
        } finally {
            if (em != null) em.close();
        }
    }

    public boolean isPhoneUnique(String phone) {
        EntityManager em = null;
        try {
            em = EntityManagerConfiguration.getEntityManager();
            return em.createNativeQuery(VERIFY_PHONE)
                    .setParameter(1, phone)
                    .getResultList()
                    .isEmpty();
        } catch (Exception e) {
            LOGGER.error("Phone verification failed", e);
            return false;
        } finally {
            if (em != null) em.close();
        }
    }
}
