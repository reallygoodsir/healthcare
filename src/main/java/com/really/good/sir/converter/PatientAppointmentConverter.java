package com.really.good.sir.converter;


import com.really.good.sir.dto.PatientAppointmentDTO;
import com.really.good.sir.entity.PatientAppointmentEntity;

import java.sql.Date;
import java.sql.Time;

public class PatientAppointmentConverter {

    public PatientAppointmentEntity convert(final PatientAppointmentDTO dto) {
        PatientAppointmentEntity entity = new PatientAppointmentEntity();
        entity.setAppointmentId(dto.getAppointmentId());
        entity.setPatientId(dto.getPatientId());
        entity.setServiceId(dto.getServiceId());
        entity.setDoctorId(dto.getDoctorId()); // <-- Added this
        entity.setStatus(dto.getStatus());

        entity.setDate(parseDate(dto.getDate()));
        entity.setStartTime(parseTime(dto.getStartTime()));
        entity.setEndTime(parseTime(dto.getEndTime()));

        return entity;
    }

    public PatientAppointmentDTO convert(final PatientAppointmentEntity entity) {
        PatientAppointmentDTO dto = new PatientAppointmentDTO();
        dto.setAppointmentId(entity.getAppointmentId());
        dto.setPatientId(entity.getPatientId());
        dto.setServiceId(entity.getServiceId());
        dto.setDoctorId(entity.getDoctorId()); // <-- Added this
        dto.setStatus(entity.getStatus());

        dto.setDate(entity.getDate() != null ? entity.getDate().toString() : null);
        dto.setStartTime(entity.getStartTime() != null ? entity.getStartTime().toString() : null);
        dto.setEndTime(entity.getEndTime() != null ? entity.getEndTime().toString() : null);

        // Set extra fields
        dto.setPatientFirstName(entity.getPatientFirstName());
        dto.setPatientLastName(entity.getPatientLastName());
        dto.setServiceName(entity.getServiceName());

        return dto;
    }

    private Time parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return null;
        if (!timeStr.contains(":")) throw new IllegalArgumentException("Invalid time format: " + timeStr);
        if (timeStr.split(":").length == 2) timeStr += ":00"; // add seconds if missing
        return Time.valueOf(timeStr);
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        return Date.valueOf(dateStr);
    }
}
