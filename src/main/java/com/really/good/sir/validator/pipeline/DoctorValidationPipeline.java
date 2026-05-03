package com.really.good.sir.validator.pipeline;

import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.validator.DoctorValidationStrategy;
import com.really.good.sir.validator.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DoctorValidationPipeline {

    private final List<DoctorValidationStrategy> strategies;

    @Autowired
    public DoctorValidationPipeline(List<DoctorValidationStrategy> strategies) {
        this.strategies = strategies;
    }

    public ValidationResult validate(DoctorDTO doctorDTO) {
        for (DoctorValidationStrategy strategy : strategies) {
            ValidationResult result = strategy.validate(doctorDTO);

            if (!result.isValid()) {
                return result;
            }
        }

        return ValidationResult.ok();
    }
}