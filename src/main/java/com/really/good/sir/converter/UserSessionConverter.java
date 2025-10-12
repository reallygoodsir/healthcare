package com.really.good.sir.converter;

import com.really.good.sir.dto.UserSessionDTO;
import com.really.good.sir.entity.UserSessionEntity;

public class UserSessionConverter {
    public UserSessionDTO convert(UserSessionEntity entity) {
        UserSessionDTO dto = new UserSessionDTO();
        dto.setId(entity.getId());
        dto.setCredentialId(entity.getCredentialId());
        dto.setLoginDateTime(entity.getLoginDateTime());
        dto.setRole(entity.getRole());
        return dto;
    }

    public UserSessionEntity convert(UserSessionDTO dto) {
        UserSessionEntity entity = new UserSessionEntity();
        entity.setId(dto.getId());
        entity.setCredentialId(dto.getCredentialId());
        entity.setLoginDateTime(dto.getLoginDateTime());
        entity.setRole(dto.getRole());
        return entity;
    }
}
