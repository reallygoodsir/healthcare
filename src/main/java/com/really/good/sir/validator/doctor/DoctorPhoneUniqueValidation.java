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
@Order(9)
public class DoctorPhoneUniqueValidation implements DoctorValidationStrategy {

    private final CredentialDAO credentialDAO;
    private final DoctorDAO doctorDAO;

    @Autowired
    public DoctorPhoneUniqueValidation(CredentialDAO credentialDAO, DoctorDAO doctorDAO) {
        this.credentialDAO = credentialDAO;
        this.doctorDAO = doctorDAO;
    }

    @Override
    public ValidationResult validate(DoctorDTO doctorDTO) {
        int credentialId = credentialDAO.getCredentialIdByPhone(doctorDTO.getPhone());
        Integer doctorIdByCredential = doctorDAO.getDoctorIdByCredentialId(credentialId);

        if (doctorDTO.getId() == null || !doctorDTO.getId().equals(doctorIdByCredential)) {
            if (!credentialDAO.isPhoneUnique(doctorDTO.getPhone())) {
                return ValidationResult.fail("Phone number already exist");
            }
        }

        return ValidationResult.ok();
    }
}