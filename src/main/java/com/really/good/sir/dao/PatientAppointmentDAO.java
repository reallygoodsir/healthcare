package com.really.good.sir.dao;

import com.really.good.sir.config.EntityManagerConfiguration;
import com.really.good.sir.entity.DoctorEntity;
import com.really.good.sir.entity.PatientAppointmentEntity;
import com.really.good.sir.entity.ServiceEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatientAppointmentDAO {
    private static final Logger LOGGER = LogManager.getLogger(PatientAppointmentDAO.class);


    public PatientAppointmentEntity createAppointment(PatientAppointmentEntity entity) {
        EntityManager em = EntityManagerConfiguration.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception exception) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            LOGGER.error("Error while creating appointment", exception);
            return null;
        } finally {
            em.close();
        }
    }

    public List<PatientAppointmentEntity> getAllAppointments() {
        EntityManager em = EntityManagerConfiguration.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM PatientAppointmentEntity p",
                    PatientAppointmentEntity.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public List<PatientAppointmentEntity> getAppointmentsByDoctorId(int doctorId) {
        EntityManager em = EntityManagerConfiguration.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM PatientAppointmentEntity p WHERE p.doctorId = :doctorId",
                            PatientAppointmentEntity.class
                    ).setParameter("doctorId", doctorId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<PatientAppointmentEntity> getTodaysAppointmentsByDoctor(int doctorId) {
        EntityManager em = EntityManagerConfiguration.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM PatientAppointmentEntity p " +
                                    "WHERE p.doctorId = :doctorId AND p.date = CURRENT_DATE",
                            PatientAppointmentEntity.class
                    ).setParameter("doctorId", doctorId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public String getAppointmentStatusById(int appointmentId) {
        EntityManager em = EntityManagerConfiguration.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p.status FROM PatientAppointmentEntity p WHERE p.appointmentId = :id",
                            String.class
                    ).setParameter("id", appointmentId)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public List<List<Object>> getAppointmentDetailsByPatientId(int patientId) {
        EntityManager em = EntityManagerConfiguration.getEntityManager();
        List<List<Object>> result = new ArrayList<>();

        try {
            String sql = "SELECT pa.appointment_id, pa.patient_id, pa.service_id, pa.doctor_id, pa.date, pa.start_time, pa.end_time, pa.status, " +
                    "       d.doctor_id AS doc_id, d.first_name AS doctor_first_name, d.last_name AS doctor_last_name, d.specialization_id, d.photo, d.credential_id, " +
                    "       s.id AS service_id, s.name AS service_name, s.price " +
                    "FROM patient_appointments pa " +
                    "JOIN doctors d ON pa.doctor_id = d.doctor_id " +
                    "JOIN service s ON pa.service_id = s.id " +
                    "WHERE pa.patient_id = :patientId " +
                    "ORDER BY pa.date DESC, pa.start_time DESC";

            List<Object[]> queryResult = em.createNativeQuery(sql).setParameter("patientId", patientId).getResultList();

            for (Object[] row : queryResult) {
                PatientAppointmentEntity appointment = new PatientAppointmentEntity();
                appointment.setAppointmentId(((Number) row[0]).intValue());
                appointment.setPatientId(((Number) row[1]).intValue());
                appointment.setServiceId(((Number) row[2]).intValue());
                appointment.setDoctorId(((Number) row[3]).intValue());
                appointment.setDate((Date) row[4]);
                appointment.setStartTime((Time) row[5]);
                appointment.setEndTime((Time) row[6]);
                appointment.setStatus((String) row[7]);

                DoctorEntity doctor = new DoctorEntity();
                doctor.setId(((Number) row[8]).intValue());
                doctor.setFirstName((String) row[9]);
                doctor.setLastName((String) row[10]);
                doctor.setSpecializationId(((Number) row[11]).intValue());
                doctor.setPhoto((byte[]) row[12]);

                ServiceEntity service = new ServiceEntity();
                service.setId(((Number) row[13]).intValue());
                service.setName((String) row[14]);
                service.setPrice(((Number) row[15]).intValue());

                result.add(Arrays.asList(appointment, doctor, service));
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching appointment details for patientId={}", patientId, e);
        } finally {
            em.close();
        }

        return result;
    }


    public boolean updateStatus(int appointmentId, String status) {
        EntityManager em = EntityManagerConfiguration.getEntityManager();
        try {
            em.getTransaction().begin();
            int updated = em.createQuery(
                            "UPDATE PatientAppointmentEntity p " +
                                    "SET p.status = :status WHERE p.appointmentId = :id"
                    ).setParameter("status", status)
                    .setParameter("id", appointmentId)
                    .executeUpdate();
            em.getTransaction().commit();
            return updated > 0;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }


    public boolean deleteAppointment(int appointmentId) {
        EntityManager em = EntityManagerConfiguration.getEntityManager();
        try {
            em.getTransaction().begin();
            PatientAppointmentEntity entity =
                    em.find(PatientAppointmentEntity.class, appointmentId);
            if (entity == null) return false;
            em.remove(entity);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }


    public boolean hasOverlappingAppointment(
            int doctorId, Date date, Time startTime, Time endTime) {

        EntityManager em = EntityManagerConfiguration.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(p) FROM PatientAppointmentEntity p " +
                                    "WHERE p.doctorId = :doctorId AND p.date = :date " +
                                    "AND ((p.startTime < :endTime AND p.endTime > :startTime) " +
                                    "OR (p.startTime >= :startTime AND p.startTime < :endTime))",
                            Long.class
                    ).setParameter("doctorId", doctorId)
                    .setParameter("date", date)
                    .setParameter("startTime", startTime)
                    .setParameter("endTime", endTime)
                    .getSingleResult();

            return count > 0;
        } finally {
            em.close();
        }
    }
}
