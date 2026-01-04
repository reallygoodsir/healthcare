package com.really.good.sir.dao;

import com.really.good.sir.entity.PatientEntity;
import com.really.good.sir.entity.CredentialEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class PatientDAO {

    private static final Logger LOGGER = LogManager.getLogger(PatientDAO.class);

    public PatientEntity createPatient(PatientEntity patientEntity) {
        EntityManager entityManager = EntityManagerProvider.getEntityManager();
        try {
            entityManager.getTransaction().begin();

            CredentialEntity credential = patientEntity.getCredentialEntity();
            entityManager.persist(credential);
            entityManager.persist(patientEntity);

            entityManager.getTransaction().commit();
            return patientEntity;

        } catch (Exception exception) {
            if (entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
            LOGGER.error("Error creating patient", exception);
            return null;
        } finally {
            entityManager.close();
        }
    }

    public List<PatientEntity> getAllPatients() {
        EntityManager entityManager = EntityManagerProvider.getEntityManager();
        try {
            return entityManager.createQuery(
                    "SELECT DISTINCT p FROM PatientEntity p " +
                            "LEFT JOIN FETCH p.credentialEntity",
                    PatientEntity.class
            ).getResultList();
        } finally {
            entityManager.close();
        }
    }

    public PatientEntity getPatientById(int patientId) {
        EntityManager entityManager = EntityManagerProvider.getEntityManager();
        try {
            List<PatientEntity> results = entityManager.createQuery(
                            "SELECT p FROM PatientEntity p " +
                                    "LEFT JOIN FETCH p.credentialEntity " +
                                    "WHERE p.id = :id",
                            PatientEntity.class
                    ).setParameter("id", patientId)
                    .getResultList();

            return results.isEmpty() ? null : results.getFirst();

        } finally {
            entityManager.close();
        }
    }

    public PatientEntity getPatientByPhone(String phone) {
        EntityManager entityManager = EntityManagerProvider.getEntityManager();
        try {
            List<PatientEntity> results = entityManager.createQuery(
                            "SELECT p FROM PatientEntity p " +
                                    "LEFT JOIN FETCH p.credentialEntity c " +
                                    "WHERE c.phone = :phone",
                            PatientEntity.class
                    ).setParameter("phone", phone)
                    .getResultList();

            return results.isEmpty() ? null : results.getFirst();

        } finally {
            entityManager.close();
        }
    }

    public boolean updatePatient(PatientEntity patientEntity) {
        EntityManager entityManager = EntityManagerProvider.getEntityManager();
        try {
            entityManager.getTransaction().begin();

            PatientEntity managedPatient = entityManager.find(
                    PatientEntity.class,
                    patientEntity.getId()
            );
            if (managedPatient == null) {
                return false;
            }

            managedPatient.setFirstName(patientEntity.getFirstName());
            managedPatient.setLastName(patientEntity.getLastName());
            managedPatient.setDateOfBirth(patientEntity.getDateOfBirth());
            managedPatient.setAddress(patientEntity.getAddress());

            CredentialEntity managedCredential = managedPatient.getCredentialEntity();
            CredentialEntity incomingCredential = patientEntity.getCredentialEntity();

            if (incomingCredential != null && managedCredential != null) {
                if (incomingCredential.getEmail() != null) {
                    managedCredential.setEmail(incomingCredential.getEmail());
                }
                if (incomingCredential.getPhone() != null) {
                    managedCredential.setPhone(incomingCredential.getPhone());
                }
            }
            entityManager.getTransaction().commit();
            return true;

        } catch (Exception exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            LOGGER.error("Error updating patient", exception);
            return false;
        } finally {
            entityManager.close();
        }
    }

    // DELETE
    public boolean deletePatient(int patientId) {
        EntityManager entityManager = EntityManagerProvider.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            PatientEntity patient = entityManager.find(PatientEntity.class, patientId);
            if (patient != null) {
                entityManager.remove(patient);
            }
            entityManager.getTransaction().commit();
            return true;
        } catch (Exception exception) {
            if (entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
            LOGGER.error("Error deleting patient {}", patientId, exception);
            return false;
        } finally {
            entityManager.close();
        }
    }

    // LOOKUP
    public int getPatientIdByCredentialId(int credentialId) {
        EntityManager entityManager = EntityManagerProvider.getEntityManager();
        try {
            TypedQuery<Integer> query = entityManager.createQuery(
                    "SELECT p.id FROM PatientEntity p WHERE p.credentialEntity.credentialId = :cid",
                    Integer.class
            );
            query.setParameter("cid", credentialId);
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }
}
