package com.really.good.sir.service;

import com.really.good.sir.converter.PatientAppointmentOutcomeConverter;
import com.really.good.sir.dao.PatientAppointmentOutcomeDAO;
import com.really.good.sir.dto.PatientAppointmentOutcomeDTO;
import com.really.good.sir.entity.PatientAppointmentOutcomeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientAppointmentOutcomeService {

    private final PatientAppointmentOutcomeDAO dao;
    private final PatientAppointmentOutcomeConverter converter;

    @Autowired
    public PatientAppointmentOutcomeService(PatientAppointmentOutcomeDAO dao,
                                            PatientAppointmentOutcomeConverter converter) {
        this.dao = dao;
        this.converter = converter;
    }

    public PatientAppointmentOutcomeDTO getOutcomeByAppointmentId(Integer appointmentId){
        PatientAppointmentOutcomeEntity entity = dao.getOutcomeByAppointmentId(appointmentId);
        return converter.convert(entity);
    }

    public PatientAppointmentOutcomeDTO saveOrUpdateOutcome(PatientAppointmentOutcomeDTO dto) throws Exception {
        PatientAppointmentOutcomeEntity entity = converter.convert(dto);
        PatientAppointmentOutcomeEntity result = dao.saveOrUpdateOutcome(entity);
        return converter.convert(result);
    }
}