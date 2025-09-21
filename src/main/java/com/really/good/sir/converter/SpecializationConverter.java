package com.really.good.sir.converter;

import com.really.good.sir.dto.SpecializationDTO;
import com.really.good.sir.entity.SpecializationEntity;

import java.util.ArrayList;
import java.util.List;

public class SpecializationConverter {
    public SpecializationEntity convert(final SpecializationDTO specializationDTO) {
        final SpecializationEntity specializationEntity = new SpecializationEntity();
        specializationEntity.setId(specializationDTO.getId());
        specializationEntity.setName(specializationDTO.getName());
        return specializationEntity;
    }

    public SpecializationDTO convert(final SpecializationEntity specializationEntity) {
        final SpecializationDTO specializationDTO = new SpecializationDTO();
        specializationDTO.setId(specializationEntity.getId());
        specializationDTO.setName(specializationEntity.getName());
        return specializationDTO;
    }

    public List<SpecializationDTO> convert(final List<SpecializationEntity> doctorEntities) {
        final List<SpecializationDTO> specializationDTOs = new ArrayList<>();
        for (SpecializationEntity specializationEntity : doctorEntities) {
            final SpecializationDTO specializationDTO = convert(specializationEntity);
            specializationDTOs.add(specializationDTO);
        }
        return specializationDTOs;
    }
}
