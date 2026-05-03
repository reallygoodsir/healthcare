package com.really.good.sir.validation.group;

import javax.validation.GroupSequence;

@GroupSequence({
        NameValidGroup.class,
        NameUniqueGroup.class,
        PriceValidGroup.class
})
public interface CreateServiceValidation {
}