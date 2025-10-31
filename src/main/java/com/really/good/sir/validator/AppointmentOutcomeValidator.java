package com.really.good.sir.validator;

import com.really.good.sir.dto.AppointmentOutcomeDTO;

public class AppointmentOutcomeValidator {

    public boolean isDiagnosisValid(AppointmentOutcomeDTO outcome) {
        String diagnosis = outcome.getDiagnosis();
        return diagnosis != null && !diagnosis.trim().isEmpty();
    }

    public boolean isRecommendationsValid(AppointmentOutcomeDTO outcome) {
        String recommendations = outcome.getRecommendations();
        return recommendations != null && !recommendations.trim().isEmpty();
    }
}
