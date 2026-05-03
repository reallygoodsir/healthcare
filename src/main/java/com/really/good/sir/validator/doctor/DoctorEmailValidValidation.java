package com.really.good.sir.validator.doctor;

import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.validator.DoctorValidationStrategy;
import com.really.good.sir.validator.ValidationResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(6)
public class DoctorEmailValidValidation implements DoctorValidationStrategy {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @Override
    public ValidationResult validate(DoctorDTO doctorDTO) {
        String email = doctorDTO.getEmail();

        if (email == null || !email.matches(EMAIL_REGEX)) {
            return ValidationResult.fail("Email has the wrong format");
        }

        return ValidationResult.ok();
    }
}