package com.really.good.sir.service;

import com.really.good.sir.converter.SpecializationConverter;
import com.really.good.sir.dao.SpecializationDAO;
import com.really.good.sir.dto.SpecializationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SpecializationService {

    private final SpecializationDAO dao;
    private final SpecializationConverter converter;

    @Autowired
    public SpecializationService(SpecializationDAO dao, SpecializationConverter converter) {
        this.dao = dao;
        this.converter = converter;
    }

    public List<SpecializationDTO> getAllSpecializations(){
        return converter.convert(dao.getAllSpecializations());
    }

    public SpecializationDTO getSpecializationById(int specializationId){
        return converter.convert(dao.getSpecializationById(specializationId));
    }
}
