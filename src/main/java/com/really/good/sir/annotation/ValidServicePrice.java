package com.really.good.sir.annotation;

import com.really.good.sir.validator.ServicePriceValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ServicePriceValidator.class)
@Documented
public @interface ValidServicePrice {

    String message() default "Price has to be over 0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}