package com.really.good.sir.service;

import com.really.good.sir.converter.PatientAppointmentOutcomeConverter;
import com.really.good.sir.dao.PatientAppointmentOutcomeDAO;
import com.really.good.sir.dto.PatientAppointmentOutcomeDTO;
import com.really.good.sir.entity.PatientAppointmentOutcomeEntity;

public class PatientAppointmentOutcomeService {
    private final PatientAppointmentOutcomeDAO patientAppointmentOutcomeDAO = new PatientAppointmentOutcomeDAO();
    private final PatientAppointmentOutcomeConverter patientAppointmentOutcomeConverter = new PatientAppointmentOutcomeConverter();

    public PatientAppointmentOutcomeDTO getOutcomeByAppointmentId(Integer appointmentId){
        PatientAppointmentOutcomeEntity entity = patientAppointmentOutcomeDAO.getOutcomeByAppointmentId(appointmentId);
        return patientAppointmentOutcomeConverter.convert(entity);
    }

    public PatientAppointmentOutcomeDTO saveOrUpdateOutcome(PatientAppointmentOutcomeDTO patientAppointmentOutcomeDTO) throws Exception {
        PatientAppointmentOutcomeEntity entity = patientAppointmentOutcomeDAO.saveOrUpdateOutcome(patientAppointmentOutcomeDTO);
        return patientAppointmentOutcomeConverter.convert(entity);
    }
}
