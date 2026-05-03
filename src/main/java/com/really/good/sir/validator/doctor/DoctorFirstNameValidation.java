package com.really.good.sir.validator.doctor;

import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.validator.DoctorValidationStrategy;
import com.really.good.sir.validator.ValidationResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class DoctorFirstNameValidation implements DoctorValidationStrategy {

    private static final String NAME_REGEX = "^[A-Za-z\\s'-]{2,25}$";

    @Override
    public ValidationResult validate(DoctorDTO doctorDTO) {
        String firstName = doctorDTO.getFirstName();

        if (firstName == null || !firstName.matches(NAME_REGEX)) {
            return ValidationResult.fail("First name has the wrong format");
        }

        return ValidationResult.ok();
    }
}