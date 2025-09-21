package com.really.good.sir.converter;

import com.really.good.sir.dto.DoctorScheduleDTO;
import com.really.good.sir.entity.DoctorScheduleEntity;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DoctorScheduleConverter {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public DoctorScheduleEntity convert(final DoctorScheduleDTO dto) {
        final DoctorScheduleEntity entity = new DoctorScheduleEntity();
        entity.setId(dto.getId());
        entity.setDoctorId(dto.getDoctorId());
        entity.setScheduleDate(Date.valueOf(dto.getScheduleDate()));
        try {
            entity.setStartTime(new Time(TIME_FORMAT.parse(dto.getStartTime()).getTime()));
            entity.setEndTime(new Time(TIME_FORMAT.parse(dto.getEndTime()).getTime()));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid time format, expected HH:mm:ss", e);
        }
        return entity;
    }

    public DoctorScheduleDTO convert(final DoctorScheduleEntity entity) {
        final DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setId(entity.getId());
        dto.setDoctorId(entity.getDoctorId());
        dto.setScheduleDate(DATE_FORMAT.format(entity.getScheduleDate()));
        dto.setStartTime(TIME_FORMAT.format(entity.getStartTime()));
        dto.setEndTime(TIME_FORMAT.format(entity.getEndTime()));
        return dto;
    }

    public List<DoctorScheduleDTO> convert(final List<DoctorScheduleEntity> doctorEntities) {
        final List<DoctorScheduleDTO> doctorDTOs = new ArrayList<>();
        for (DoctorScheduleEntity doctorScheduleEntity : doctorEntities) {
            final DoctorScheduleDTO doctorScheduleDTO = convert(doctorScheduleEntity);
            doctorDTOs.add(doctorScheduleDTO);
        }
        return doctorDTOs;
    }
}
