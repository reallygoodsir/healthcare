package com.really.good.sir.service;

import com.really.good.sir.converter.SpecializationConverter;
import com.really.good.sir.dao.SpecializationDAO;
import com.really.good.sir.dto.SpecializationDTO;
import com.really.good.sir.entity.SpecializationEntity;
import com.really.good.sir.resources.DoctorResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SpecializationService {
    private static final Logger LOGGER = LogManager.getLogger(DoctorResource.class);
    private final SpecializationConverter specializationConverter = new SpecializationConverter();
    private final SpecializationDAO specializationDAO = new SpecializationDAO();

    public List<SpecializationDTO> getAllSpecializations(){
        final List<SpecializationEntity> specializationEntities = specializationDAO.getAllSpecializations();
        return specializationConverter.convert(specializationEntities);
    }

    public SpecializationDTO getSpecializationById(int specializationId){
        SpecializationEntity entity = specializationDAO.getSpecializationById(specializationId);
        return specializationConverter.convert(entity);
    }
}
