package com.really.good.sir.converter;

import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.entity.CredentialEntity;
import com.really.good.sir.entity.DoctorEntity;

import java.util.ArrayList;
import java.util.List;

public class DoctorConverter {
    public DoctorEntity convert(final DoctorDTO doctorDTO) {
        final DoctorEntity doctorEntity = new DoctorEntity();
        if(doctorDTO.getId() != null) doctorEntity.setId(doctorDTO.getId());
        doctorEntity.setFirstName(doctorDTO.getFirstName());
        doctorEntity.setLastName(doctorDTO.getLastName());
        CredentialEntity credentialEntity = new CredentialEntity();
        credentialEntity.setEmail(doctorDTO.getEmail());
        credentialEntity.setPhone(doctorDTO.getPhone());
        doctorEntity.setCredentialEntity(credentialEntity);
        doctorEntity.setSpecializationId(doctorDTO.getSpecializationId());
        doctorEntity.setPhoto(doctorDTO.getPhoto());
        return doctorEntity;
    }

    public DoctorDTO convert(final DoctorEntity doctorEntity) {
        final DoctorDTO doctorDTO = new DoctorDTO();
        if (doctorEntity.getId() != null) doctorDTO.setId(doctorEntity.getId());
        doctorDTO.setFirstName(doctorEntity.getFirstName());
        doctorDTO.setLastName(doctorEntity.getLastName());

        if (doctorEntity.getCredentialEntity() != null) {
            doctorDTO.setEmail(doctorEntity.getCredentialEntity().getEmail());
            doctorDTO.setPhone(doctorEntity.getCredentialEntity().getPhone());
        }

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
