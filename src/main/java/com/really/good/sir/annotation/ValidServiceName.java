package com.really.good.sir.annotation;

import com.really.good.sir.validator.ServiceNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ServiceNameValidator.class)
@Documented
public @interface ValidServiceName {

    String message() default "Service name has the wrong format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}