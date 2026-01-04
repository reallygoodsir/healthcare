package com.really.good.sir.converter;

import com.really.good.sir.dto.PatientDTO;
import com.really.good.sir.entity.PatientEntity;
import com.really.good.sir.entity.CredentialEntity;
import com.really.good.sir.entity.Role;
import com.really.good.sir.service.PasswordGenerator;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PatientConverter {

    private final PasswordGenerator passwordGenerator = new PasswordGenerator();

    public PatientEntity convert(final PatientDTO dto) {
        if (dto == null) return null;

        PatientEntity entity = new PatientEntity();
        if (dto.getId() != null) entity.setId(dto.getId());

        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setAddress(dto.getAddress());
        entity.setDateOfBirth(Date.valueOf(dto.getDateOfBirth()));

        CredentialEntity credential = new CredentialEntity();
        credential.setEmail(dto.getEmail());
        credential.setPhone(dto.getPhone());
        credential.setRole(Role.valueOf("PATIENT"));
        credential.setPasswordHash(passwordGenerator.hashPassword());

        entity.setCredentialEntity(credential);

        return entity;
    }

    public PatientDTO convert(final PatientEntity entity) {
        if (entity == null) return null;

        PatientDTO dto = new PatientDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setAddress(entity.getAddress());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dto.setDateOfBirth(sdf.format(entity.getDateOfBirth()));

        if (entity.getCredentialEntity() != null) {
            dto.setEmail(entity.getCredentialEntity().getEmail());
            dto.setPhone(entity.getCredentialEntity().getPhone());
        }

        return dto;
    }

    public List<PatientDTO> convert(final List<PatientEntity> entities) {
        List<PatientDTO> list = new ArrayList<>();
        for (PatientEntity entity : entities) {
            list.add(convert(entity));
        }
        return list;
    }
}
