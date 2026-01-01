package com.really.good.sir.dao;

import com.really.good.sir.entity.AppointmentEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class AppointmentDAO {

    private static final Logger LOGGER = LogManager.getLogger(AppointmentDAO.class);

    // =========================
    // persist
    // =========================
    public AppointmentEntity createAppointment(AppointmentEntity entity) {
        EntityManager em = null;
        try {
            em = EntityManagerProvider.getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            LOGGER.error("Error creating appointment", e);
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    // =========================
    // find
    // =========================
    public AppointmentEntity getAppointmentById(int appointmentId) {
        EntityManager em = null;
        try {
            em = EntityManagerProvider.getEntityManager();
            return em.find(AppointmentEntity.class, appointmentId);
        } catch (Exception e) {
            LOGGER.error("Error fetching appointment with id {}", appointmentId, e);
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    // =========================
    // criteria api
    // =========================
    public List<AppointmentEntity> getAllAppointments() {
        EntityManager em = null;
        try {
            em = EntityManagerProvider.getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<AppointmentEntity> cq = cb.createQuery(AppointmentEntity.class);
            Root<AppointmentEntity> root = cq.from(AppointmentEntity.class);

            cq.select(root);

            TypedQuery<AppointmentEntity> query = em.createQuery(cq);
            return query.getResultList();

        } catch (Exception e) {
            LOGGER.error("Error fetching appointments", e);
            return List.of();
        } finally {
            if (em != null) em.close();
        }
    }

    // =========================
    // named query
    // =========================
    public boolean updateAppointmentStatus(int appointmentId, String status) {
        EntityManager em = null;
        try {
            em = EntityManagerProvider.getEntityManager();
            em.getTransaction().begin();

            int updated = em.createNamedQuery("Appointment.updateStatus")
                    .setParameter("status", status)
                    .setParameter("id", appointmentId)
                    .executeUpdate();

            em.getTransaction().commit();
            return updated > 0;

        } catch (Exception e) {
            LOGGER.error("Error updating appointment status for id {}", appointmentId, e);
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            if (em != null) em.close();
        }
    }

    // =========================
    // native query
    // =========================
    public boolean deleteAppointment(int appointmentId) {
        EntityManager em = null;
        try {
            em = EntityManagerProvider.getEntityManager();
            em.getTransaction().begin();

            int deleted = em.createNativeQuery(
                            "DELETE FROM appointments WHERE appointment_id = ?")
                    .setParameter(1, appointmentId)
                    .executeUpdate();

            em.getTransaction().commit();
            return deleted > 0;

        } catch (Exception e) {
            LOGGER.error("Error deleting appointment with id {}", appointmentId, e);
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            if (em != null) em.close();
        }
    }
}
