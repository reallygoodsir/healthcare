package com.really.good.sir.validator;

import com.really.good.sir.dao.PatientAppointmentDAO;
import com.really.good.sir.dto.PatientAppointmentDTO;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PatientAppointmentValidator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final PatientAppointmentDAO patientAppointmentDAO = new PatientAppointmentDAO();

    public boolean isDateValid(PatientAppointmentDTO appointment) {
        String date = appointment.getDate();
        if (date == null || date.isEmpty()) {
            return false;
        }
        try {
            LocalDate appointmentDate = LocalDate.parse(date, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            // appointment date must be today or in the future
            return appointmentDate.isEqual(today) || appointmentDate.isAfter(today);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public boolean isTimeRangeValid(PatientAppointmentDTO appointment) {
        String start = appointment.getStartTime();
        String end = appointment.getEndTime();
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

    public boolean isOverlapping(PatientAppointmentDTO appointment) {
        if (appointment.getDoctorId() <= 0 || appointment.getDate() == null || appointment.getDate().isEmpty()) {
            return false;
        }
        try {
            // convert DTO values to java.sql.Date / java.sql.Time for SQL method
            Date sqlDate = Date.valueOf(appointment.getDate());
            Time sqlStart = Time.valueOf(LocalTime.parse(appointment.getStartTime(), TIME_FORMATTER));
            Time sqlEnd = Time.valueOf(LocalTime.parse(appointment.getEndTime(), TIME_FORMATTER));

            return patientAppointmentDAO.hasOverlappingAppointment(
                    appointment.getDoctorId(),
                    sqlDate,
                    sqlStart,
                    sqlEnd
            );
        } catch (Exception e) {
            return false;
        }
    }
}
