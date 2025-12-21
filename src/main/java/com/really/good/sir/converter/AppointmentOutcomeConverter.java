package com.really.good.sir.converter;

import com.really.good.sir.dto.AppointmentOutcomeDTO;
import com.really.good.sir.entity.AppointmentOutcomeEntity;

import java.util.ArrayList;
import java.util.List;

public class AppointmentOutcomeConverter {

    public AppointmentOutcomeEntity convert(final AppointmentOutcomeDTO dto) {
        final AppointmentOutcomeEntity entity = new AppointmentOutcomeEntity();
        if(dto.getAppointmentId() != null) entity.setAppointmentId(dto.getAppointmentId());
        if(dto.getDiagnosis() != null) entity.setDiagnosis(dto.getDiagnosis());
        if(dto.getRecommendations() != null) entity.setRecommendations(dto.getRecommendations());
        if(dto.getCreatedAt() != null) entity.setCreatedAt(dto.getCreatedAt());
        if(dto.getUpdatedAt() != null) entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    public AppointmentOutcomeDTO convert(final AppointmentOutcomeEntity entity) {
        if(entity == null) return null;
        final AppointmentOutcomeDTO dto = new AppointmentOutcomeDTO();
        if(entity.getAppointmentId() != null) dto.setAppointmentId(entity.getAppointmentId());
        if(entity.getDiagnosis() != null) dto.setDiagnosis(entity.getDiagnosis());
        if(entity.getRecommendations() != null) dto.setRecommendations(entity.getRecommendations());
        if(entity.getCreatedAt() != null) dto.setCreatedAt(entity.getCreatedAt());
        if(entity.getUpdatedAt() != null) dto.setUpdatedAt(entity.getUpdatedAt());
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
