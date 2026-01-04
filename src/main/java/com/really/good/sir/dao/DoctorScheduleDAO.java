package com.really.good.sir.dao;

import com.really.good.sir.entity.DoctorScheduleEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.Date;
import java.util.List;

public class DoctorScheduleDAO {

    private static final Logger LOGGER = LogManager.getLogger(DoctorScheduleDAO.class);

    // =====================================================
    // GET schedules by doctor
    // =====================================================
    public List<DoctorScheduleEntity> getSchedulesByDoctor(final int doctorId) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            TypedQuery<DoctorScheduleEntity> query =
                    em.createQuery(
                            "SELECT ds FROM DoctorScheduleEntity ds " +
                                    "WHERE ds.doctorId = :doctorId " +
                                    "ORDER BY ds.scheduleDate, ds.startTime",
                            DoctorScheduleEntity.class);

            query.setParameter("doctorId", doctorId);
            return query.getResultList();

        } catch (Exception e) {
            LOGGER.error("Error getting schedules for doctor {}", doctorId, e);
            return List.of();
        } finally {
            em.close();
        }
    }

    // =====================================================
    // EXISTS check
    // =====================================================
    public boolean scheduleExists(int scheduleId) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT COUNT(ds.id) FROM DoctorScheduleEntity ds WHERE ds.id = :id");
            query.setParameter("id", scheduleId);

            Long count = (Long) query.getSingleResult();
            return count > 0;

        } catch (Exception e) {
            LOGGER.error("Error checking schedule existence", e);
            return false;
        } finally {
            em.close();
        }
    }

    // =====================================================
    // CREATE
    // =====================================================
    public DoctorScheduleEntity createSchedule(final DoctorScheduleEntity schedule) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(schedule);
            em.getTransaction().commit();
            return schedule;

        } catch (Exception e) {
            LOGGER.error("Error creating schedule", e);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return schedule; // same behavior as JDBC version
        } finally {
            em.close();
        }
    }

    // =====================================================
    // UPDATE
    // =====================================================
    public boolean updateSchedule(final DoctorScheduleEntity schedule) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            em.getTransaction().begin();

            Query query = em.createQuery(
                    "UPDATE DoctorScheduleEntity ds " +
                            "SET ds.scheduleDate = :date, " +
                            "    ds.startTime = :start, " +
                            "    ds.endTime = :end " +
                            "WHERE ds.id = :id");

            query.setParameter("date", schedule.getScheduleDate());
            query.setParameter("start", schedule.getStartTime());
            query.setParameter("end", schedule.getEndTime());
            query.setParameter("id", schedule.getId());

            int updated = query.executeUpdate();
            em.getTransaction().commit();

            return updated > 0;

        } catch (Exception e) {
            LOGGER.error("Error updating schedule {}", schedule.getId(), e);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    // =====================================================
    // DELETE
    // =====================================================
    public boolean deleteSchedule(final int id) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            em.getTransaction().begin();

            Query query = em.createQuery(
                    "DELETE FROM DoctorScheduleEntity ds WHERE ds.id = :id");
            query.setParameter("id", id);

            int deleted = query.executeUpdate();
            em.getTransaction().commit();

            return deleted > 0;

        } catch (Exception e) {
            LOGGER.error("Error deleting schedule {}", id, e);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    // =====================================================
    // TODAY schedules WITH appointments (native SQL, unchanged semantics)
    // =====================================================
    public List<DoctorScheduleEntity> getSchedulesForTodayWithAppointments(final int doctorId) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            String sql =
                    "SELECT ds.* " +
                            "FROM doctor_schedules ds " +
                            "INNER JOIN appointments a ON ds.id = a.schedule_id " +
                            "WHERE ds.doctor_id = ? " +
                            "AND a.status <> 'CANCELLED' " +
                            "AND ds.schedule_date = CURRENT_DATE " +
                            "ORDER BY ds.start_time";

            @SuppressWarnings("unchecked")
            List<DoctorScheduleEntity> result =
                    em.createNativeQuery(sql, DoctorScheduleEntity.class)
                            .setParameter(1, doctorId)
                            .getResultList();

            return result;

        } catch (Exception e) {
            LOGGER.error("Error getting today's schedules with appointments for doctor {}", doctorId, e);
            return List.of();
        } finally {
            em.close();
        }
    }

    // =====================================================
    // BY doctor AND date
    // =====================================================
    public List<DoctorScheduleEntity> getSchedulesByDoctorAndDate(
            final int doctorId,
            final String scheduleDate) {

        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            TypedQuery<DoctorScheduleEntity> query =
                    em.createQuery(
                            "SELECT ds FROM DoctorScheduleEntity ds " +
                                    "WHERE ds.doctorId = :doctorId " +
                                    "AND ds.scheduleDate = :date " +
                                    "ORDER BY ds.startTime",
                            DoctorScheduleEntity.class);

            query.setParameter("doctorId", doctorId);
            query.setParameter("date", Date.valueOf(scheduleDate));

            return query.getResultList();

        } catch (Exception e) {
            LOGGER.error("Error getting schedules for doctor {} on date {}", doctorId, scheduleDate, e);
            return List.of();
        } finally {
            em.close();
        }
    }
}
