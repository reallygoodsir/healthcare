package com.really.good.sir.validator;

import com.really.good.sir.annotation.ValidServicePrice;
import com.really.good.sir.dto.ServiceDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ServicePriceValidator
        implements ConstraintValidator<ValidServicePrice, ServiceDTO> {

    @Override
    public boolean isValid(ServiceDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        boolean valid = dto.getPrice() != null && dto.getPrice() > 0;

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Price has to be over 0")
                    .addPropertyNode("price")
                    .addConstraintViolation();
        }

        return valid;
    }
}