package com.really.good.sir.validator.doctor;

import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.validator.DoctorValidationStrategy;
import com.really.good.sir.validator.ValidationResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class DoctorLastNameValidation implements DoctorValidationStrategy {

    private static final String NAME_REGEX = "^[A-Za-z\\s'-]{2,25}$";

    @Override
    public ValidationResult validate(DoctorDTO doctorDTO) {
        String lastName = doctorDTO.getLastName();

        if (lastName == null || !lastName.matches(NAME_REGEX)) {
            return ValidationResult.fail("Last name has the wrong format");
        }

        return ValidationResult.ok();
    }
}