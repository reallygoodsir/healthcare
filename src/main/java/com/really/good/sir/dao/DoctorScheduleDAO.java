package com.really.good.sir.dao;

import com.really.good.sir.entity.DoctorScheduleEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorScheduleDAO extends BaseDao {
    private static final Logger LOGGER = LogManager.getLogger(DoctorScheduleDAO.class);

    private static final String GET_SCHEDULES_BY_DOCTOR =
            "SELECT * FROM doctor_schedules WHERE doctor_id = ? ORDER BY schedule_date, start_time";

    private static final String CREATE_SCHEDULE =
            "INSERT INTO doctor_schedules (doctor_id, schedule_date, start_time, end_time) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_SCHEDULE =
            "UPDATE doctor_schedules SET schedule_date = ?, start_time = ?, end_time = ? WHERE id = ?";

    private static final String DELETE_SCHEDULE =
            "DELETE FROM doctor_schedules WHERE id = ?";

    public List<DoctorScheduleEntity> getSchedulesByDoctor(final int doctorId) {
        final List<DoctorScheduleEntity> schedules = new ArrayList<>();
        try (final Connection connection = getConnection();
             final PreparedStatement ps = connection.prepareStatement(GET_SCHEDULES_BY_DOCTOR)) {
            ps.setInt(1, doctorId);
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        } catch (final SQLException exception) {
            LOGGER.error("Error getting schedules for doctor {}", doctorId, exception);
        }
        return schedules;
    }

    public DoctorScheduleEntity createSchedule(final DoctorScheduleEntity schedule) {
        try (final Connection connection = getConnection();
             final PreparedStatement ps = connection.prepareStatement(CREATE_SCHEDULE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, schedule.getDoctorId());
            ps.setDate(2, schedule.getScheduleDate());
            ps.setTime(3, schedule.getStartTime());
            ps.setTime(4, schedule.getEndTime());
            ps.executeUpdate();
            try (final ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) schedule.setId(rs.getInt(1));
            }
        } catch (final SQLException exception) {
            LOGGER.error("Error creating schedule", exception);
        }
        return schedule;
    }

    public boolean updateSchedule(final DoctorScheduleEntity schedule) {
        try (final Connection connection = getConnection();
             final PreparedStatement ps = connection.prepareStatement(UPDATE_SCHEDULE)) {
            ps.setDate(1, schedule.getScheduleDate());
            ps.setTime(2, schedule.getStartTime());
            ps.setTime(3, schedule.getEndTime());
            ps.setInt(4, schedule.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException exception) {
            LOGGER.error("Error updating schedule {}", schedule.getId(), exception);
        }
        return false;
    }

    public boolean deleteSchedule(final int id) {
        try (final Connection connection = getConnection();
             final PreparedStatement ps = connection.prepareStatement(DELETE_SCHEDULE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (final SQLException exception) {
            LOGGER.error("Error deleting schedule {}", id, exception);
        }
        return false;
    }

    private DoctorScheduleEntity mapResultSetToSchedule(final ResultSet rs) throws SQLException {
        final DoctorScheduleEntity entity = new DoctorScheduleEntity();
        entity.setId(rs.getInt("id"));
        entity.setDoctorId(rs.getInt("doctor_id"));
        entity.setScheduleDate(rs.getDate("schedule_date"));
        entity.setStartTime(rs.getTime("start_time"));
        entity.setEndTime(rs.getTime("end_time"));
        return entity;
    }
}
