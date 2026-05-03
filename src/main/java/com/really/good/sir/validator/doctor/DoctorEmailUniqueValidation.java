package com.really.good.sir.validator.doctor;

import com.really.good.sir.dao.CredentialDAO;
import com.really.good.sir.dao.DoctorDAO;
import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.validator.DoctorValidationStrategy;
import com.really.good.sir.validator.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(7)
public class DoctorEmailUniqueValidation implements DoctorValidationStrategy {

    private final CredentialDAO credentialDAO;
    private final DoctorDAO doctorDAO;

    @Autowired
    public DoctorEmailUniqueValidation(CredentialDAO credentialDAO, DoctorDAO doctorDAO) {
        this.credentialDAO = credentialDAO;
        this.doctorDAO = doctorDAO;
    }

    @Override
    public ValidationResult validate(DoctorDTO doctorDTO) {
        int credentialId = credentialDAO.getCredentialIdByEmail(doctorDTO.getEmail());
        Integer doctorIdByCredential = doctorDAO.getDoctorIdByCredentialId(credentialId);

        if (doctorDTO.getId() == null || !doctorDTO.getId().equals(doctorIdByCredential)) {
            if (!credentialDAO.isEmailUnique(doctorDTO.getEmail())) {
                return ValidationResult.fail("Email already exists");
            }
        }

        return ValidationResult.ok();
    }
}