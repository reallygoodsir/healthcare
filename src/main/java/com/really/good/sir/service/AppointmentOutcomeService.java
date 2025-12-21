package com.really.good.sir.service;

import com.really.good.sir.converter.AppointmentOutcomeConverter;
import com.really.good.sir.dao.AppointmentOutcomeDAO;
import com.really.good.sir.dto.AppointmentOutcomeDTO;
import com.really.good.sir.entity.AppointmentOutcomeEntity;

public class AppointmentOutcomeService {
    private final AppointmentOutcomeDAO outcomeDAO = new AppointmentOutcomeDAO();
    private final AppointmentOutcomeConverter outcomeConverter = new AppointmentOutcomeConverter();

    public AppointmentOutcomeDTO saveOrUpdateOutcome(AppointmentOutcomeDTO appointmentOutcomeDTO) {
        AppointmentOutcomeEntity entity = outcomeConverter.convert(appointmentOutcomeDTO);
        AppointmentOutcomeEntity entityResponse = outcomeDAO.saveOrUpdateOutcome(entity);
        return outcomeConverter.convert(entityResponse);
    }

    public AppointmentOutcomeDTO getOutcomeByAppointmentId(int appointmentId) {
        AppointmentOutcomeEntity entity = outcomeDAO.getOutcomeByAppointmentId(appointmentId);
        return outcomeConverter.convert(entity);
    }
}
