package com.really.good.sir.dao;

import com.really.good.sir.dto.PatientAppointmentDetailsDTO;
import com.really.good.sir.entity.PatientAppointmentEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.really.good.sir.dao.BaseDao.getConnection;

public class PatientAppointmentDAO {
    private static final Logger LOGGER = LogManager.getLogger(PatientAppointmentDAO.class);


    public PatientAppointmentEntity createAppointment(PatientAppointmentEntity entity) {
        EntityManager em = EntityManagerProvider.getEntityManager();
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
        EntityManager em = EntityManagerProvider.getEntityManager();
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
        EntityManager em = EntityManagerProvider.getEntityManager();
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
        EntityManager em = EntityManagerProvider.getEntityManager();
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
        EntityManager em = EntityManagerProvider.getEntityManager();
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

    public List<PatientAppointmentDetailsDTO> getAppointmentDetailsByPatientId(int patientId) {
        List<PatientAppointmentDetailsDTO> list = new ArrayList<>();

        String sql = "SELECT pa.appointment_id, pa.date, pa.start_time, pa.end_time, pa.status, " +
                "       d.doctor_id, d.first_name AS doctor_first_name, d.last_name AS doctor_last_name, " +
                "       s.id AS service_id, s.name AS service_name " +
                "FROM patient_appointments pa " +
                "JOIN doctors d ON pa.doctor_id = d.doctor_id " +
                "JOIN service s ON pa.service_id = s.id " +
                "WHERE pa.patient_id = ? " +
                "ORDER BY pa.date DESC, pa.start_time DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PatientAppointmentDetailsDTO dto = new PatientAppointmentDetailsDTO();
                    dto.setAppointmentId(rs.getInt("appointment_id"));
                    dto.setDate(rs.getString("date"));
                    dto.setStartTime(rs.getString("start_time"));
                    dto.setEndTime(rs.getString("end_time"));
                    dto.setStatus(rs.getString("status"));
                    dto.setDoctorId(rs.getInt("doctor_id"));
                    dto.setDoctorFirstName(rs.getString("doctor_first_name"));
                    dto.setDoctorLastName(rs.getString("doctor_last_name"));
                    dto.setServiceId(rs.getInt("service_id"));
                    dto.setServiceName(rs.getString("service_name"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching appointment details for patientId={}", patientId, e);
        }
        return list;
    }


    /* -------------------- UPDATE -------------------- */

    public boolean updateStatus(int appointmentId, String status) {
        EntityManager em = EntityManagerProvider.getEntityManager();
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

    /* -------------------- DELETE -------------------- */

    public boolean deleteAppointment(int appointmentId) {
        EntityManager em = EntityManagerProvider.getEntityManager();
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

    /* -------------------- BUSINESS CHECK -------------------- */

    public boolean hasOverlappingAppointment(
            int doctorId, Date date, Time startTime, Time endTime) {

        EntityManager em = EntityManagerProvider.getEntityManager();
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
