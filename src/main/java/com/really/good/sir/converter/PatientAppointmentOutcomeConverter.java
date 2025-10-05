package com.really.good.sir.converter;

import com.really.good.sir.dto.PatientAppointmentOutcomeDTO;
import com.really.good.sir.entity.PatientAppointmentOutcomeEntity;

public class PatientAppointmentOutcomeConverter {

    public PatientAppointmentOutcomeDTO convert(PatientAppointmentOutcomeEntity entity) {
        if (entity == null) return null;
        PatientAppointmentOutcomeDTO dto = new PatientAppointmentOutcomeDTO();
        dto.setId(entity.getId());
        dto.setAppointmentId(entity.getAppointmentId());
        dto.setResult(entity.getResult());
        return dto;
    }

    public PatientAppointmentOutcomeEntity convert(PatientAppointmentOutcomeDTO dto) {
        if (dto == null) return null;
        PatientAppointmentOutcomeEntity entity = new PatientAppointmentOutcomeEntity();
        entity.setId(dto.getId());
        entity.setAppointmentId(dto.getAppointmentId());
        entity.setResult(dto.getResult());
        return entity;
    }
}
