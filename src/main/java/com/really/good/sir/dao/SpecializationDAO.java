package com.really.good.sir.dao;

import com.really.good.sir.entity.SpecializationEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
@Repository
public class SpecializationDAO {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    
    private static final Logger LOGGER = LogManager.getLogger(SpecializationDAO.class);

    // Named Query example
    public List<SpecializationEntity> getAllSpecializations() {
        EntityManager em = null;
        try {
            em = entityManagerFactory.createEntityManager();

            // Named query defined in orm.xml
            TypedQuery<SpecializationEntity> query = em.createNamedQuery("Specialization.findAll", SpecializationEntity.class);
            return query.getResultList();

        } catch (Exception e) {
            LOGGER.error("Error fetching all specializations", e);
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    // Native Query example
    public SpecializationEntity getSpecializationById(int id) {
        EntityManager em = null;
        try {
            em = entityManagerFactory.createEntityManager();

            // Using native SQL query
            return (SpecializationEntity) em.createNativeQuery(
                            "SELECT * FROM specializations WHERE id = ?", SpecializationEntity.class)
                    .setParameter(1, id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } catch (Exception e) {
            LOGGER.error("Error fetching specialization by id {}", id, e);
            return null;
        } finally {
            if (em != null) em.close();
        }
    }
}
