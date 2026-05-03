package com.really.good.sir.validator.doctor;

import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.validator.DoctorValidationStrategy;
import com.really.good.sir.validator.ValidationResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(8)
public class DoctorPhoneValidValidation implements DoctorValidationStrategy {

    private static final String PHONE_REGEX = "^[+()\\d\\s-]{7,20}$";

    @Override
    public ValidationResult validate(DoctorDTO doctorDTO) {
        String phone = doctorDTO.getPhone();

        if (phone == null || !phone.matches(PHONE_REGEX)) {
            return ValidationResult.fail("Phone number has the wrong format");
        }

        return ValidationResult.ok();
    }
}