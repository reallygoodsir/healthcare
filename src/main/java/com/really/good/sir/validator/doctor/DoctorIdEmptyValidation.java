package com.really.good.sir.validator.doctor;

import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.validator.DoctorValidationStrategy;
import com.really.good.sir.validator.ValidationResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DoctorIdEmptyValidation implements DoctorValidationStrategy {

    @Override
    public ValidationResult validate(DoctorDTO doctorDTO) {
        if (doctorDTO.getId() != null) {
            return ValidationResult.fail("Doctor id must be empty when new doctor is created");
        }

        return ValidationResult.ok();
    }
}