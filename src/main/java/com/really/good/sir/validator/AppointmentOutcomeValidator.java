package com.really.good.sir.validator;

import com.really.good.sir.converter.AppointmentOutcomeConverter;
import com.really.good.sir.dao.AppointmentOutcomeDAO;
import com.really.good.sir.dto.AppointmentOutcomeDTO;
import com.really.good.sir.entity.AppointmentOutcomeEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppointmentOutcomeValidator {
    private final AppointmentOutcomeConverter outcomeConverter = new AppointmentOutcomeConverter();
    private static final Logger LOGGER = LogManager.getLogger(AppointmentOutcomeValidator.class);
    private final AppointmentOutcomeDAO appointmentOutcomeDAO = new AppointmentOutcomeDAO();

    public boolean isAppointmentIdValid(AppointmentOutcomeDTO outcome) {
        try {
            AppointmentOutcomeEntity entity = appointmentOutcomeDAO.getOutcomeByAppointmentId(outcome.getAppointmentId());
            AppointmentOutcomeDTO dto = outcomeConverter.convert(entity);
            return dto != null;
        }catch (Exception exception){
            LOGGER.warn("Appointment id validity check exception");
            return false;
        }
    }

    public boolean isAppointmentIdEmpty(AppointmentOutcomeDTO outcome) {
        try {
            Integer appointmentId = outcome.getAppointmentId();
            return appointmentId == null;
        }catch (Exception exception){
            LOGGER.warn("Appointment id existence check exception");
            return false;
        }
    }

    public boolean isDiagnosisValid(AppointmentOutcomeDTO outcome) {
        String diagnosis = outcome.getDiagnosis();
        return diagnosis != null && !diagnosis.trim().isEmpty();
    }

    public boolean isRecommendationsValid(AppointmentOutcomeDTO outcome) {
        String recommendations = outcome.getRecommendations();
        return recommendations != null && !recommendations.trim().isEmpty();
    }
}
