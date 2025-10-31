package com.really.good.sir.validator;

import com.really.good.sir.dao.CredentialDAO;
import com.really.good.sir.dao.DoctorDAO;
import com.really.good.sir.dto.DoctorDTO;

public class DoctorValidator {

    private static final String NAME_REGEX = "^[A-Za-z\\s'-]{2,25}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^[+()\\d\\s-]{7,20}$";
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final CredentialDAO credentialDAO = new CredentialDAO();

    public boolean isFirstNameValid(DoctorDTO doctor) {
        String firstName = doctor.getFirstName();
        return firstName != null && firstName.matches(NAME_REGEX);
    }

    public boolean isLastNameValid(DoctorDTO doctor) {
        String lastName = doctor.getLastName();
        return lastName != null && lastName.matches(NAME_REGEX);
    }

    public boolean isEmailValid(DoctorDTO doctor) {
        String email = doctor.getEmail();
        if (email == null || !email.matches(EMAIL_REGEX)) {
            return false;
        }
        int credentialId = credentialDAO.getCredentialIdByEmail(email);
        if (doctor.getId() != doctorDAO.getDoctorIdByCredentialId(credentialId)) {
            return credentialDAO.isEmailUnique(email);
        }
        return true;
    }

    public boolean isPhoneValid(DoctorDTO doctor) {
        String phone = doctor.getPhone();
        if (phone == null || !phone.matches(PHONE_REGEX)) {
            return false;
        }
        int credentialId = credentialDAO.getCredentialIdByPhone(phone);
        if (doctor.getId() != doctorDAO.getDoctorIdByCredentialId(credentialId)) {
            return credentialDAO.isPhoneUnique(phone);
        }
        return true;
    }

    public boolean isSpecializationIdValid(DoctorDTO doctor) {
        return doctor.getSpecializationId() > 0;
    }
}
