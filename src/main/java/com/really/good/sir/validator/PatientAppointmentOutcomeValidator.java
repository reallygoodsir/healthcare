package com.really.good.sir.validator;

import com.really.good.sir.dto.PatientAppointmentOutcomeDTO;

public class PatientAppointmentOutcomeValidator {

    public boolean isResultValid(PatientAppointmentOutcomeDTO outcome) {
        String result = outcome.getResult();
        return result != null && !result.trim().isEmpty();
    }
}
