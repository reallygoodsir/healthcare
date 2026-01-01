package com.really.good.sir.converter;

import com.really.good.sir.dto.ServiceDTO;
import com.really.good.sir.entity.ServiceEntity;

import java.util.ArrayList;
import java.util.List;

public class ServiceConverter {

    public ServiceEntity convert(final ServiceDTO dto) {
        if (dto == null) return null;
        ServiceEntity entity = new ServiceEntity();
        if (dto.getId() != null && dto.getId() > 0) {
            entity.setId(dto.getId());
        }
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        return entity;
    }

    public ServiceDTO convert(final ServiceEntity entity) {
        if (entity == null) return null;
        ServiceDTO dto = new ServiceDTO();
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
