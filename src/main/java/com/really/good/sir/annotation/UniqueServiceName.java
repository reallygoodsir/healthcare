package com.really.good.sir.annotation;

import com.really.good.sir.validator.UniqueServiceNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueServiceNameValidator.class)
@Documented
public @interface UniqueServiceName {

    String message() default "Service name already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}