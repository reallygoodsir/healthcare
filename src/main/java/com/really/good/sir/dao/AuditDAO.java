package com.really.good.sir.dao;

import com.really.good.sir.entity.AuditEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Repository
public class AuditDAO {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private static final Logger LOGGER = LogManager.getLogger(AuditDAO.class);

    public boolean createAudit(AuditEntity auditEntity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            entityManager.persist(auditEntity);

            entityManager.getTransaction().commit();
            return true;

        } catch (Exception exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            LOGGER.error("Error creating audit record", exception);
            return false;

        } finally {
            entityManager.close();
        }
    }
}