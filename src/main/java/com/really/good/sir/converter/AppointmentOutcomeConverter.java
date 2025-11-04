package com.really.good.sir.converter;

import com.really.good.sir.dto.AppointmentOutcomeDTO;
import com.really.good.sir.entity.AppointmentOutcomeEntity;

import java.util.ArrayList;
import java.util.List;

public class AppointmentOutcomeConverter {

    public AppointmentOutcomeEntity convert(final AppointmentOutcomeDTO dto) {
        final AppointmentOutcomeEntity entity = new AppointmentOutcomeEntity();
        entity.setAppointmentId(dto.getAppointmentId());
        entity.setDiagnosis(dto.getDiagnosis());
        entity.setRecommendations(dto.getRecommendations());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    public AppointmentOutcomeDTO convert(final AppointmentOutcomeEntity entity) {
        final AppointmentOutcomeDTO dto = new AppointmentOutcomeDTO();
        dto.setAppointmentId(entity.getAppointmentId());
        dto.setDiagnosis(entity.getDiagnosis());
        dto.setRecommendations(entity.getRecommendations());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public List<AppointmentOutcomeDTO> convert(final List<AppointmentOutcomeEntity> entities) {
        final List<AppointmentOutcomeDTO> dtos = new ArrayList<>();
        for (AppointmentOutcomeEntity entity : entities) {
            dtos.add(convert(entity));
        }
        return dtos;
    }
}
