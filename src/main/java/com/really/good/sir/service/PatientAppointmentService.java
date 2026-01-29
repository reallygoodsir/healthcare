package com.really.good.sir.service;

import com.really.good.sir.converter.PatientAppointmentConverter;
import com.really.good.sir.converter.PatientAppointmentDetailsConverter;
import com.really.good.sir.dao.PatientAppointmentDAO;
import com.really.good.sir.dto.PatientAppointmentDTO;
import com.really.good.sir.dto.PatientAppointmentDetailsDTO;
import com.really.good.sir.entity.PatientAppointmentEntity;

import java.util.List;

public class PatientAppointmentService {
    private final PatientAppointmentDAO dao = new PatientAppointmentDAO();
    private final PatientAppointmentConverter converter = new PatientAppointmentConverter();
    private final PatientAppointmentDetailsConverter detailsConverter = new PatientAppointmentDetailsConverter();

    public List<PatientAppointmentDTO> getAllAppointments() {
        List<PatientAppointmentEntity> list = dao.getAllAppointments();
        return list.stream().map(converter::convert).toList();
    }

    public String getAppointmentStatusById(Integer appointmentId) {
        return dao.getAppointmentStatusById(appointmentId);
    }

    public List<PatientAppointmentDetailsDTO> getAppointmentDetailsByPatientId(Integer patientId) {
        List<List<Object>> appointmentDetailsByPatientId = dao.getAppointmentDetailsByPatientId(patientId);
        return detailsConverter.convert(appointmentDetailsByPatientId);
    }

    public List<PatientAppointmentDTO> getAppointmentsByDoctorId(int doctorId) {
        List<PatientAppointmentEntity> list = dao.getAppointmentsByDoctorId(doctorId);
        return list.stream().map(converter::convert).toList();
    }

    public List<PatientAppointmentDTO> getTodaysAppointmentsByDoctor(Integer doctorId) {
        List<PatientAppointmentEntity> list = dao.getTodaysAppointmentsByDoctor(doctorId);
        return list.stream().map(converter::convert).toList();
    }

    public PatientAppointmentDTO createAppointment(PatientAppointmentDTO patientAppointmentDTO) {
        PatientAppointmentEntity entity = converter.convert(patientAppointmentDTO);
        PatientAppointmentEntity appointment = dao.createAppointment(entity);
        return converter.convert(appointment);
    }


    public boolean updateStatus(int appointmentId, String status) {
        return dao.updateStatus(appointmentId, status);
    }

    public boolean deleteAppointment(int appointmentId) {
        return dao.deleteAppointment(appointmentId);
    }
}
