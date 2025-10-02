package com.really.good.sir.converter;

import com.really.good.sir.dto.ServiceDTO;
import com.really.good.sir.entity.ServiceEntity;

import java.util.ArrayList;
import java.util.List;

public class ServiceConverter {

    public ServiceEntity convert(final ServiceDTO dto) {
        final ServiceEntity entity = new ServiceEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        return entity;
    }

    public ServiceDTO convert(final ServiceEntity entity) {
        final ServiceDTO dto = new ServiceDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        return dto;
    }

    public List<ServiceDTO> convert(final List<ServiceEntity> entities) {
        final List<ServiceDTO> dtos = new ArrayList<>();
        for (ServiceEntity entity : entities) {
            dtos.add(convert(entity));
        }
        return dtos;
    }
}
