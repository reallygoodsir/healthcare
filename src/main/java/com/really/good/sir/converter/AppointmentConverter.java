package com.really.good.sir.converter;

import com.really.good.sir.dto.AppointmentDTO;
import com.really.good.sir.entity.AppointmentEntity;

import java.util.ArrayList;
import java.util.List;

public class AppointmentConverter {

    public AppointmentEntity convert(AppointmentDTO dto) {
        AppointmentEntity entity = new AppointmentEntity();
        entity.setAppointmentId(dto.getAppointmentId());
        entity.setPatientId(dto.getPatientId());
        entity.setDoctorId(dto.getDoctorId());
        entity.setScheduleId(dto.getScheduleId());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    public AppointmentDTO convert(AppointmentEntity entity) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAppointmentId(entity.getAppointmentId());
        dto.setPatientId(entity.getPatientId());
        dto.setDoctorId(entity.getDoctorId());
        dto.setScheduleId(entity.getScheduleId());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    public List<AppointmentDTO> convert(List<AppointmentEntity> entities) {
        List<AppointmentDTO> dtos = new ArrayList<>();
        for (AppointmentEntity e : entities) {
            dtos.add(convert(e));
        }
        return dtos;
    }
}
