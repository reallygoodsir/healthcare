package com.really.good.sir.service;

import com.really.good.sir.converter.PatientAppointmentConverter;
import com.really.good.sir.converter.PatientAppointmentDetailsConverter;
import com.really.good.sir.dao.PatientAppointmentDAO;
import com.really.good.sir.dto.PatientAppointmentDTO;
import com.really.good.sir.dto.PatientAppointmentDetailsDTO;
import com.really.good.sir.entity.PatientAppointmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PatientAppointmentService {

    private final PatientAppointmentDAO dao;
    private final PatientAppointmentConverter converter;
    private final PatientAppointmentDetailsConverter detailsConverter;

    @Autowired
    public PatientAppointmentService(PatientAppointmentDAO dao,
                                     PatientAppointmentConverter converter,
                                     PatientAppointmentDetailsConverter detailsConverter) {
        this.dao = dao;
        this.converter = converter;
        this.detailsConverter = detailsConverter;
    }

    public List<PatientAppointmentDTO> getAllAppointments() {
        return dao.getAllAppointments()
                .stream()
                .map(converter::convert)
                .toList();
    }

    public String getAppointmentStatusById(Integer appointmentId) {
        return dao.getAppointmentStatusById(appointmentId);
    }

    public List<PatientAppointmentDetailsDTO> getAppointmentDetailsByPatientId(Integer patientId) {
        List<List<Object>> details = dao.getAppointmentDetailsByPatientId(patientId);
        return detailsConverter.convert(details);
    }

    public List<PatientAppointmentDTO> getAppointmentsByDoctorId(int doctorId) {
        return dao.getAppointmentsByDoctorId(doctorId)
                .stream()
                .map(converter::convert)
                .toList();
    }

    public List<PatientAppointmentDTO> getTodaysAppointmentsByDoctor(Integer doctorId) {
        return dao.getTodaysAppointmentsByDoctor(doctorId)
                .stream()
                .map(converter::convert)
                .toList();
    }

    public PatientAppointmentDTO createAppointment(PatientAppointmentDTO dto) {
        PatientAppointmentEntity entity = converter.convert(dto);
        PatientAppointmentEntity created = dao.createAppointment(entity);
        return converter.convert(created);
    }

    public boolean updateStatus(int appointmentId, String status) {
        return dao.updateStatus(appointmentId, status);
    }

    public boolean deleteAppointment(int appointmentId) {
        return dao.deleteAppointment(appointmentId);
    }
}