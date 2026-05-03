package com.really.good.sir.validator.doctor;

import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.validator.DoctorValidationStrategy;
import com.really.good.sir.validator.ValidationResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class DoctorPhotoValidation implements DoctorValidationStrategy {

    @Override
    public ValidationResult validate(DoctorDTO doctorDTO) {
        if (doctorDTO.getPhoto() == null || doctorDTO.getPhoto().length == 0) {
            return ValidationResult.fail("Photo is not valid");
        }

        return ValidationResult.ok();
    }
}