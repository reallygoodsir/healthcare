package com.really.good.sir.dao;

import com.really.good.sir.entity.ServiceEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import java.util.List;
import javax.persistence.*;
import javax.persistence.criteria.*;

public class ServiceDAO {

    private static final Logger LOGGER = LogManager.getLogger(ServiceDAO.class);

    /* =========================
       CREATE
       ========================= */
    public ServiceEntity createService(ServiceEntity service) {
        EntityManager em = null;
        try {
            em = EntityManagerProvider.getEntityManager();
            em.getTransaction().begin();

            service.setId(null); // required for IDENTITY
            em.persist(service);
            em.flush();          // ensures IDENTITY is generated
            em.getTransaction().commit();

            return service;
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            LOGGER.error("Error creating service", e);
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    /* =========================
       READ - ALL (JPQL)
       ========================= */
    public List<ServiceEntity> getAllServices() {
        EntityManager em = null;
        try {
            em = EntityManagerProvider.getEntityManager();
            TypedQuery<ServiceEntity> query = em.createQuery("SELECT s FROM ServiceEntity s", ServiceEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error fetching all services", e);
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    /* =========================
       READ - BY ID (EntityManager.find)
       ========================= */
    public ServiceEntity getServiceById(Integer id) {
        EntityManager em = null;
        try {
            em = EntityManagerProvider.getEntityManager();
            return em.find(ServiceEntity.class, id);
        } catch (Exception e) {
            LOGGER.error("Error fetching service by id {}", id, e);
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    /* =========================
       UPDATE (EntityManager.merge)
       ========================= */
    public boolean updateService(ServiceEntity service) {
        EntityManager em = null;
        try {
            em = EntityManagerProvider.getEntityManager();
            em.getTransaction().begin();

            em.merge(service);  // merge updates the entity in DB
            em.getTransaction().commit();

            return true;
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            LOGGER.error("Error updating service", e);
            return false;
        } finally {
            if (em != null) em.close();
        }
    }

    /* =========================
       DELETE
       ========================= */
    public boolean deleteService(Integer id) {
        EntityManager em = null;
        try {
            em = EntityManagerProvider.getEntityManager();
            em.getTransaction().begin();

            ServiceEntity entity = em.find(ServiceEntity.class, id);
            if (entity != null) {
                em.remove(entity);
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            LOGGER.error("Error deleting service", e);
            return false;
        } finally {
            if (em != null) em.close();
        }
    }

    /* =========================
       CHECK SERVICE NAME EXISTS (Criteria API)
       ========================= */
    public boolean isServiceNameExists(String name, Integer excludeId) {
        EntityManager em = null;
        try {
            em = EntityManagerProvider.getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);

            Root<ServiceEntity> root = cq.from(ServiceEntity.class);
            cq.select(cb.count(root));

            if (excludeId != null && excludeId > 0) {
                cq.where(cb.and(
                        cb.equal(root.get("name"), name),
                        cb.notEqual(root.get("id"), excludeId)
                ));
            } else {
                cq.where(cb.equal(root.get("name"), name));
            }

            Long count = em.createQuery(cq).getSingleResult();
            return count > 0;

        } catch (Exception e) {
            LOGGER.error("Error checking service name existence", e);
            return false;
        } finally {
            if (em != null) em.close();
        }
    }
}