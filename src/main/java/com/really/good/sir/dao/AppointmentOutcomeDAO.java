package com.really.good.sir.dao;

import com.really.good.sir.entity.AppointmentOutcomeEntity;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class AppointmentOutcomeDAO {

    public AppointmentOutcomeEntity saveOrUpdateOutcome(AppointmentOutcomeEntity entity) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            em.getTransaction().begin();

            AppointmentOutcomeEntity existing =
                    em.find(AppointmentOutcomeEntity.class, entity.getAppointmentId());

            AppointmentOutcomeEntity result;
            if (existing == null) {
                em.persist(entity);                 // persist
                result = entity;
            } else {
                existing.setDiagnosis(entity.getDiagnosis());
                existing.setRecommendations(entity.getRecommendations());
                result = em.merge(existing);        // merge
            }

            em.getTransaction().commit();
            return result;
        } finally {
            em.close();
        }
    }

    public AppointmentOutcomeEntity getOutcomeByAppointmentId(int appointmentId) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            TypedQuery<AppointmentOutcomeEntity> query =
                    em.createNamedQuery(
                            "AppointmentOutcome.findByAppointmentId",
                            AppointmentOutcomeEntity.class);

            query.setParameter("appointmentId", appointmentId);

            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    public AppointmentOutcomeEntity getOutcomeByAppointmentIdCriteria(int appointmentId) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<AppointmentOutcomeEntity> cq =
                    cb.createQuery(AppointmentOutcomeEntity.class);

            Root<AppointmentOutcomeEntity> root = cq.from(AppointmentOutcomeEntity.class);

            cq.select(root)
                    .where(cb.equal(root.get("appointmentId"), appointmentId));

            return em.createQuery(cq)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    public AppointmentOutcomeEntity getOutcomeByAppointmentIdNative(int appointmentId) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            return (AppointmentOutcomeEntity) em.createNativeQuery(
                            "SELECT * FROM appointment_outcomes WHERE appointment_id = ?",
                            AppointmentOutcomeEntity.class)
                    .setParameter(1, appointmentId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }
}
