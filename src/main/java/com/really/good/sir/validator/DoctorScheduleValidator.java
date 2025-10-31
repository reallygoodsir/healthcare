package com.really.good.sir.validator;

import com.really.good.sir.dao.DoctorScheduleDAO;
import com.really.good.sir.dto.DoctorScheduleDTO;
import com.really.good.sir.entity.DoctorScheduleEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DoctorScheduleValidator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final DoctorScheduleDAO doctorScheduleDAO = new DoctorScheduleDAO();

    public boolean isScheduleDateValid(DoctorScheduleDTO schedule) {
        String scheduleDate = schedule.getScheduleDate();
        if (scheduleDate == null || scheduleDate.isEmpty()) {
            return false;
        }
        try {
            LocalDate date = LocalDate.parse(scheduleDate, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            return date.isEqual(today) || date.isAfter(today);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public boolean isTimeRangeValid(DoctorScheduleDTO schedule) {
        String start = schedule.getStartTime();
        String end = schedule.getEndTime();
        if (start == null || end == null || start.isEmpty() || end.isEmpty()) {
            return false;
        }
        try {
            LocalTime startTime = LocalTime.parse(start, TIME_FORMATTER);
            LocalTime endTime = LocalTime.parse(end, TIME_FORMATTER);
            return endTime.isAfter(startTime);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public boolean isOverlapping(DoctorScheduleDTO schedule) {
        if (schedule.getDoctorId() <= 0 || schedule.getScheduleDate() == null || schedule.getScheduleDate().isEmpty()) {
            return false;
        }
        try {
            List<DoctorScheduleEntity> existingSchedules =
                    doctorScheduleDAO.getSchedulesByDoctorAndDate(schedule.getDoctorId(), schedule.getScheduleDate());

            LocalTime newStart = LocalTime.parse(schedule.getStartTime(), TIME_FORMATTER);
            LocalTime newEnd = LocalTime.parse(schedule.getEndTime(), TIME_FORMATTER);

            for (DoctorScheduleEntity existing : existingSchedules) {
                if (existing.getId() == schedule.getId()) {
                    continue;
                }

                LocalTime existingStart = existing.getStartTime().toLocalTime();
                LocalTime existingEnd = existing.getEndTime().toLocalTime();

                boolean overlaps =
                        (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart));

                if (overlaps) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
