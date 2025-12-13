package com.really.good.sir.validator;

import com.really.good.sir.dao.AppointmentDAO;
import com.really.good.sir.dao.DoctorDAO;
import com.really.good.sir.dao.DoctorScheduleDAO;
import com.really.good.sir.dao.PatientDAO;
import com.really.good.sir.dto.AppointmentDTO;
import com.really.good.sir.dto.AppointmentOutcomeDTO;
import com.really.good.sir.dto.PatientDTO;
import com.really.good.sir.entity.AppointmentEntity;
import com.really.good.sir.entity.DoctorEntity;
import com.really.good.sir.entity.PatientEntity;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AppointmentValidator {
    private final DoctorScheduleDAO doctorScheduleDAO = new DoctorScheduleDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public boolean isScheduleIdEmpty(AppointmentDTO appointmentDTO) {
        return appointmentDTO.getScheduleId() == null;
    }

    public boolean isScheduleIdValid(AppointmentDTO appointmentDTO) {
        return doctorScheduleDAO.scheduleExists(appointmentDTO.getScheduleId());
    }

    public boolean isStatusValid(AppointmentDTO appointmentDTO) {
        return appointmentDTO.getStatus() == null || appointmentDTO.getStatus().isEmpty();
    }

    public boolean isStatusEmpty(String status) {
        return status == null || status.trim().isEmpty();
    }

    public boolean isStatusValid(String status) {
        return status.equalsIgnoreCase("COMPLETED") || status.equalsIgnoreCase("CANCELLED") || status.equalsIgnoreCase("ONGOING");
    }

    public boolean isAppointmentIdEmpty(AppointmentDTO appointmentDTO) {
        return appointmentDTO.getAppointmentId() == null;
    }

    public boolean isAppointmentIdEmpty(Integer appointmentId) {
        return appointmentId == null;
    }

    public boolean isAppointmentIdValid(Integer appointmentId) {
        AppointmentEntity entity = appointmentDAO.getAppointmentById(appointmentId);
        return entity != null && entity.getAppointmentId() != null;
    }

    public boolean isAppointmentIdValid(AppointmentOutcomeDTO outcomeDTO) {
        AppointmentEntity entity = appointmentDAO.getAppointmentById(outcomeDTO.getAppointmentId());
        return entity != null && entity.getAppointmentId() != null;
    }

    public boolean isDoctorIdEmpty(AppointmentDTO appointmentDTO) {
        Integer doctorId = appointmentDTO.getDoctorId();
        return doctorId == null;
    }

    public boolean isDoctorIdInvalid(AppointmentDTO appointmentDTO) {
        DoctorEntity doctor = doctorDAO.getDoctorById(appointmentDTO.getDoctorId());
        return doctor == null;
    }


    public boolean isPatientIdInvalid(AppointmentDTO appointmentDTO) {
        PatientEntity patient = patientDAO.getPatientById(appointmentDTO.getPatientId());
        return patient == null;
    }

    public boolean isPatientIdEmpty(AppointmentDTO appointmentDTO) {
        Integer patientId = appointmentDTO.getPatientId();
        return patientId == null;
    }

    public boolean isAppointmentDateValid(final String date) {
        if (date == null || date.isEmpty()) {
            return false;
        }
        try {
            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
