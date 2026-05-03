package com.really.good.sir.validator;

import com.really.good.sir.annotation.UniqueServiceName;
import com.really.good.sir.dto.ServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueServiceNameValidator
        implements ConstraintValidator<UniqueServiceName, ServiceDTO> {

    @Autowired
    private ServiceValidator serviceValidator;

    @Override
    public boolean isValid(ServiceDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        boolean valid = serviceValidator.isNameUnique(dto);

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Service name already exists")
                    .addPropertyNode("name")
                    .addConstraintViolation();
        }

        return valid;
    }
}