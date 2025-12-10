package com.really.good.sir.validator;

import com.really.good.sir.dao.SpecializationDAO;

public class SpecializationValidator {
    private final SpecializationDAO specializationDAO = new SpecializationDAO();

    public boolean isEmpty(final Integer id) {
        return id == null;
    }

    public boolean exists(final Integer id) {
        return specializationDAO.getSpecializationById(id) != null;
    }
}
