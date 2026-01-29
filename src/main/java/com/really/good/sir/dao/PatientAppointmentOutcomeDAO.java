package com.really.good.sir.dao;

import com.really.good.sir.config.EntityManagerConfiguration;
import com.really.good.sir.entity.PatientAppointmentOutcomeEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public class PatientAppointmentOutcomeDAO {

    private static final Logger LOGGER = LogManager.getLogger(PatientAppointmentOutcomeDAO.class);

    public PatientAppointmentOutcomeEntity getOutcomeByAppointmentId(int appointmentId) {
        EntityManager em = EntityManagerConfiguration.getEntityManager();;
        try {
            return em.createNamedQuery("PatientAppointmentOutcomeEntity.getByAppointmentId", PatientAppointmentOutcomeEntity.class)
                    .setParameter("appointmentId", appointmentId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            LOGGER.error("Failed to fetch outcome for appointmentId={}", appointmentId, e);
            return null;
        } finally {
            em.close();
        }
    }


    public PatientAppointmentOutcomeEntity saveOrUpdateOutcome(PatientAppointmentOutcomeEntity entity) throws Exception {
        EntityManager em = EntityManagerConfiguration.getEntityManager();;
        try {
            em.getTransaction().begin();

            PatientAppointmentOutcomeEntity existing = getOutcomeByAppointmentId(entity.getAppointmentId());
            if (existing == null) {
                em.persist(entity);
            } else {
                existing.setResult(entity.getResult());
                entity = em.merge(existing);
            }

            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOGGER.error("Failed to save/update outcome for appointmentId={}", entity.getAppointmentId(), e);
            throw new Exception("Failed to save/update outcome", e);
        } finally {
            em.close();
        }
    }
}
