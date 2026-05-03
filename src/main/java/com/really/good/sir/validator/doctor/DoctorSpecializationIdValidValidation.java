package com.really.good.sir.validator.doctor;

import com.really.good.sir.dao.SpecializationDAO;
import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.validator.DoctorValidationStrategy;
import com.really.good.sir.validator.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class DoctorSpecializationIdValidValidation implements DoctorValidationStrategy {

    private final SpecializationDAO specializationDAO;

    @Autowired
    public DoctorSpecializationIdValidValidation(SpecializationDAO specializationDAO) {
        this.specializationDAO = specializationDAO;
    }

    @Override
    public ValidationResult validate(DoctorDTO doctorDTO) {
        if (specializationDAO.getSpecializationById(doctorDTO.getSpecializationId()) == null) {
            return ValidationResult.fail("Specialization id does not exist");
        }

        return ValidationResult.ok();
    }
}