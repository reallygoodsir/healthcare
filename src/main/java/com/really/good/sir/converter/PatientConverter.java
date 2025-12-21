package com.really.good.sir.converter;

import com.really.good.sir.dto.PatientDTO;
import com.really.good.sir.entity.PatientEntity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PatientConverter {

    public PatientEntity convert(final PatientDTO patientDTO) {
        if (patientDTO == null) return null;
        final PatientEntity patientEntity = new PatientEntity();
        if (patientDTO.getId() != null) patientEntity.setId(patientDTO.getId());
        patientEntity.setFirstName(patientDTO.getFirstName());
        patientEntity.setLastName(patientDTO.getLastName());
        patientEntity.setEmail(patientDTO.getEmail());
        patientEntity.setPhone(patientDTO.getPhone());
        patientEntity.setDateOfBirth(Date.valueOf(patientDTO.getDateOfBirth()));
        patientEntity.setAddress(patientDTO.getAddress());
        return patientEntity;
    }

    public PatientDTO convert(final PatientEntity patientEntity) {
        if (patientEntity == null) return null;
        final PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(patientEntity.getId());
        patientDTO.setFirstName(patientEntity.getFirstName());
        patientDTO.setLastName(patientEntity.getLastName());
        patientDTO.setEmail(patientEntity.getEmail());
        patientDTO.setPhone(patientEntity.getPhone());

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String dateOfBirth = sdf.format(patientEntity.getDateOfBirth());

        patientDTO.setDateOfBirth(dateOfBirth);
        patientDTO.setAddress(patientEntity.getAddress());
        return patientDTO;
    }

    public List<PatientDTO> convert(final List<PatientEntity> patientEntities) {
        final List<PatientDTO> patientDTOs = new ArrayList<>();
        for (PatientEntity patientEntity : patientEntities) {
            final PatientDTO patientDTO = convert(patientEntity);
            patientDTOs.add(patientDTO);
        }
        return patientDTOs;
    }
}