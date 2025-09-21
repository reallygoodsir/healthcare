package com.really.good.sir.converter;

import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.entity.DoctorEntity;

import java.util.ArrayList;
import java.util.List;

public class DoctorConverter {
    public DoctorEntity convert(final DoctorDTO doctorDTO) {
        final DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setId(doctorDTO.getId());
        doctorEntity.setFirstName(doctorDTO.getFirstName());
        doctorEntity.setLastName(doctorDTO.getLastName());
        doctorEntity.setEmail(doctorDTO.getEmail());
        doctorEntity.setPhone(doctorDTO.getPhone());
        doctorEntity.setSpecializationId(doctorDTO.getSpecializationId());
        doctorEntity.setPhoto(doctorDTO.getPhoto());
        return doctorEntity;
    }

    public DoctorDTO convert(final DoctorEntity doctorEntity) {
        final DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(doctorEntity.getId());
        doctorDTO.setFirstName(doctorEntity.getFirstName());
        doctorDTO.setLastName(doctorEntity.getLastName());
        doctorDTO.setEmail(doctorEntity.getEmail());
        doctorDTO.setPhone(doctorEntity.getPhone());
        doctorDTO.setSpecializationId(doctorEntity.getSpecializationId());
        doctorDTO.setPhoto(doctorEntity.getPhoto());
        return doctorDTO;
    }

    public List<DoctorDTO> convert(final List<DoctorEntity> doctorEntities) {
        final List<DoctorDTO> doctorDTOs = new ArrayList<>();
        for (DoctorEntity doctorEntity : doctorEntities) {
            final DoctorDTO doctorDTO = convert(doctorEntity);
            doctorDTOs.add(doctorDTO);
        }
        return doctorDTOs;
    }
}
