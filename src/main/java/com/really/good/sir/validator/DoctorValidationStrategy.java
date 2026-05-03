package com.really.good.sir.validator;

import com.really.good.sir.dto.DoctorDTO;

public interface DoctorValidationStrategy {

    ValidationResult validate(DoctorDTO doctorDTO);
}