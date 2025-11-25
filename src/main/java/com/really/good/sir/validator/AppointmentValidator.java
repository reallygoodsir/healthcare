package com.really.good.sir.validator;

import com.really.good.sir.dao.DoctorDAO;
import com.really.good.sir.dao.DoctorScheduleDAO;
import com.really.good.sir.dao.PatientDAO;
import com.really.good.sir.dto.AppointmentDTO;
import com.really.good.sir.entity.DoctorEntity;
import com.really.good.sir.entity.PatientEntity;

public class AppointmentValidator {
    private final DoctorScheduleDAO doctorScheduleDAO = new DoctorScheduleDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final PatientDAO patientDAO = new PatientDAO();


    public boolean isScheduleIdValid(AppointmentDTO appointmentDTO) {
        return doctorScheduleDAO.scheduleExists(appointmentDTO.getScheduleId());
    }

    public boolean isStatusValid(AppointmentDTO appointmentDTO) {
        return appointmentDTO.getStatus() == null || appointmentDTO.getStatus().isEmpty();
    }

    public boolean isAppointmentIdValid(AppointmentDTO appointmentDTO) {
        return appointmentDTO.getAppointmentId() == null;
    }

    public boolean isDoctorIdEmpty(AppointmentDTO appointmentDTO) {
        Integer doctorId = appointmentDTO.getDoctorId();
        return doctorId == null;
    }

    public boolean isDoctorInValid(AppointmentDTO appointmentDTO) {
        DoctorEntity doctor = doctorDAO.getDoctorById(appointmentDTO.getDoctorId());
        return doctor == null;
    }


    public boolean isPatientInValid(AppointmentDTO appointmentDTO) {
        PatientEntity patient = patientDAO.getPatientById(appointmentDTO.getPatientId());
        return patient == null;
    }

    public boolean isPatientIdEmpty(AppointmentDTO appointmentDTO) {
        Integer patientId = appointmentDTO.getPatientId();
        return patientId == null;
    }

}
