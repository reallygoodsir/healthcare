package com.really.good.sir.dao;

import com.really.good.sir.entity.DoctorEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class DoctorDAO {
    private static final Logger LOGGER = LogManager.getLogger(DoctorDAO.class);

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");

    public DoctorEntity createDoctor(final DoctorEntity doctorEntity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(doctorEntity.getCredentialEntity());
            em.flush(); // ensure ID is generated
            em.persist(doctorEntity);
            em.getTransaction().commit();
            return doctorEntity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            LOGGER.error("Error creating doctor", e);
            return null;
        } finally {
            em.close();
        }
    }


    // --- GET ALL DOCTORS ---
    public List<DoctorEntity> getAllDoctors() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<DoctorEntity> query = em.createQuery("SELECT d FROM DoctorEntity d", DoctorEntity.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // --- GET DOCTOR BY ID ---
    public DoctorEntity getDoctorById(Integer doctorId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(DoctorEntity.class, doctorId);
        } finally {
            em.close();
        }
    }

    // --- GET DOCTORS BY SPECIALIZATION (or service, adapt JPQL as needed) ---
    public List<DoctorEntity> getDoctorsBySpecializationId(int specializationId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<DoctorEntity> query = em.createQuery(
                    "SELECT d FROM DoctorEntity d WHERE d.specializationId = :specId", DoctorEntity.class);
            query.setParameter("specId", specializationId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // --- UPDATE DOCTOR ---
    public DoctorEntity updateDoctor(DoctorEntity doctorEntity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Merge will update doctor and cascade update to credential
            DoctorEntity merged = em.merge(doctorEntity);

            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            LOGGER.error("Error updating doctor", e);
            return null;
        } finally {
            em.close();
        }
    }

    // --- DELETE DOCTOR ---
    public boolean deleteDoctor(Integer doctorId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            DoctorEntity doctor = em.find(DoctorEntity.class, doctorId);
            if (doctor != null) {
                em.remove(doctor);
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            LOGGER.error("Error deleting doctor", e);
            return false;
        } finally {
            em.close();
        }
    }

    // --- GET DOCTOR ID BY CREDENTIAL ID ---
    public Integer getDoctorIdByCredentialId(Integer credentialId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Integer> query = em.createQuery(
                    "SELECT d.id FROM DoctorEntity d WHERE d.credentialEntity.credentialId = :credId", Integer.class);
            query.setParameter("credId", credentialId);
            List<Integer> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }

    public List<DoctorEntity> getDoctorsByServiceId(final int serviceId) {
        EntityManager em = emf.createEntityManager();
        try {
            String sql = "SELECT d.* FROM doctors d " +
                    "WHERE d.specialization_id IN " +
                    "(SELECT ss.specialization_id FROM `service-specializations` ss WHERE ss.service_id = :serviceId)";
            @SuppressWarnings("unchecked")
            List<DoctorEntity> doctors = em.createNativeQuery(sql, DoctorEntity.class)
                    .setParameter("serviceId", serviceId)
                    .getResultList();
            return doctors;
        } finally {
            em.close();
        }
    }
}
