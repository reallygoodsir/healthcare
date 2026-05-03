package com.really.good.sir.validator.doctor;

import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.validator.DoctorValidationStrategy;
import com.really.good.sir.validator.ValidationResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class DoctorSpecializationIdEmptyValidation implements DoctorValidationStrategy {

    @Override
    public ValidationResult validate(DoctorDTO doctorDTO) {
        if (doctorDTO.getSpecializationId() == null) {
            return ValidationResult.fail("Specialization id is empty");
        }

        return ValidationResult.ok();
    }
}