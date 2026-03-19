package com.really.good.sir.validator;

import com.really.good.sir.dao.SpecializationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecializationValidator {

    private final SpecializationDAO specializationDAO;

    @Autowired
    public SpecializationValidator(SpecializationDAO specializationDAO) {
        this.specializationDAO = specializationDAO;
    }

    public boolean isEmpty(final Integer id) {
        return id == null;
    }

    public boolean exists(final Integer id) {
        return specializationDAO.getSpecializationById(id) != null;
    }
}