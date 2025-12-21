package com.really.good.sir.service;

import com.really.good.sir.converter.AppointmentConverter;
import com.really.good.sir.dao.AppointmentDAO;
import com.really.good.sir.dto.AppointmentDTO;
import com.really.good.sir.entity.AppointmentEntity;

import java.util.List;

public class AppointmentService {
    private final AppointmentConverter converter = new AppointmentConverter();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
        AppointmentEntity entity = converter.convert(appointmentDTO);
        AppointmentEntity result = appointmentDAO.createAppointment(entity);
        return converter.convert(result);
    }

    public List<AppointmentDTO> getAllAppointments() {
        List<AppointmentEntity> appointments = appointmentDAO.getAllAppointments();
        return converter.convert(appointments);
    }

    public AppointmentDTO getAppointmentById(int appointmentId) {
        AppointmentEntity entity = appointmentDAO.getAppointmentById(appointmentId);
        return converter.convert(entity);
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        return appointmentDAO.updateAppointmentStatus(appointmentId, status);
    }

    public boolean deleteAppointment(int appointmentId) {
        return appointmentDAO.deleteAppointment(appointmentId);
    }
}
