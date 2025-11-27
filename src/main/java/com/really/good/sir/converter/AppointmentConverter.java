package com.really.good.sir.converter;

import com.really.good.sir.dto.AppointmentDTO;
import com.really.good.sir.entity.AppointmentEntity;

import java.util.ArrayList;
import java.util.List;

public class AppointmentConverter {

    public AppointmentEntity convert(AppointmentDTO dto) {
        AppointmentEntity entity = new AppointmentEntity();
        if(dto.getAppointmentId() != null) entity.setAppointmentId(dto.getAppointmentId());
        if(dto.getPatientId() != null) entity.setPatientId(dto.getPatientId());
        if(dto.getDoctorId() != null) entity.setDoctorId(dto.getDoctorId());
        if(dto.getScheduleId() != null) entity.setScheduleId(dto.getScheduleId());
        if(dto.getStatus() != null) entity.setStatus(dto.getStatus());
        return entity;
    }

    public AppointmentDTO convert(AppointmentEntity entity) {
        AppointmentDTO dto = new AppointmentDTO();
        if(entity.getAppointmentId() != null) dto.setAppointmentId(entity.getAppointmentId());
        if(entity.getPatientId() != null) dto.setPatientId(entity.getPatientId());
        if(entity.getDoctorId() != null) dto.setDoctorId(entity.getDoctorId());
        if(entity.getScheduleId() != null) dto.setScheduleId(entity.getScheduleId());
        if(entity.getStatus() != null) dto.setStatus(entity.getStatus());
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
