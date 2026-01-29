package com.really.good.sir.converter;

import com.really.good.sir.dto.PatientAppointmentDetailsDTO;
import com.really.good.sir.entity.DoctorEntity;
import com.really.good.sir.entity.PatientAppointmentEntity;
import com.really.good.sir.entity.ServiceEntity;

import java.util.ArrayList;
import java.util.List;

public class PatientAppointmentDetailsConverter {

    public List<PatientAppointmentDetailsDTO> convert(List<List<Object>> rawData) {
        List<PatientAppointmentDetailsDTO> dtos = new ArrayList<>();

        if (rawData == null || rawData.isEmpty()) {
            return dtos;
        }

        for (List<Object> row : rawData) {
            if (row.size() != 3) continue; // safety check

            PatientAppointmentEntity pa = (PatientAppointmentEntity) row.get(0);
            DoctorEntity d = (DoctorEntity) row.get(1);
            ServiceEntity s = (ServiceEntity) row.get(2);

            PatientAppointmentDetailsDTO dto = new PatientAppointmentDetailsDTO();
            dto.setAppointmentId(pa.getAppointmentId());
            dto.setDate(pa.getDate() != null ? pa.getDate().toString() : null);
            dto.setStartTime(pa.getStartTime() != null ? pa.getStartTime().toString() : null);
            dto.setEndTime(pa.getEndTime() != null ? pa.getEndTime().toString() : null);
            dto.setStatus(pa.getStatus());

            dto.setDoctorId(d.getId());
            dto.setDoctorFirstName(d.getFirstName());
            dto.setDoctorLastName(d.getLastName());

            dto.setServiceId(s.getId());
            dto.setServiceName(s.getName());

            dtos.add(dto);
        }

        return dtos;
    }
}
