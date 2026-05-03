package com.really.good.sir.validator;

import com.really.good.sir.annotation.ValidServiceName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ServiceNameValidator implements ConstraintValidator<ValidServiceName, String> {

    private static final String NAME_REGEX = "^[A-Za-z0-9\\s'-]{2,50}$";

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        return name != null && name.matches(NAME_REGEX);
    }
}