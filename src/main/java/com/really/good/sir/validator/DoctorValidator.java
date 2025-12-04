package com.really.good.sir.validator;

import com.really.good.sir.dao.CredentialDAO;
import com.really.good.sir.dao.DoctorDAO;
import com.really.good.sir.dao.SpecializationDAO;
import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.entity.DoctorEntity;

public class DoctorValidator {

    private static final String NAME_REGEX = "^[A-Za-z\\s'-]{2,25}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^[+()\\d\\s-]{7,20}$";
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final CredentialDAO credentialDAO = new CredentialDAO();

    private final SpecializationDAO specializationDAO = new SpecializationDAO();

    public boolean isIdEmpty(DoctorDTO doctor) {
        return doctor.getId() == null;
    }

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
        return email != null && email.matches(EMAIL_REGEX);
    }

    public boolean isEmailUnique(DoctorDTO doctor) {
        int credentialId = credentialDAO.getCredentialIdByEmail(doctor.getEmail());
        if (doctor.getId() != doctorDAO.getDoctorIdByCredentialId(credentialId)) {
            return credentialDAO.isEmailUnique(doctor.getEmail());
        }
        return true;
    }
    
    public boolean isPhoneValid(DoctorDTO doctor) {
        String phone = doctor.getPhone();
        return phone != null && phone.matches(PHONE_REGEX);
    }

    public boolean isPhoneUnique(DoctorDTO doctor) {
        int credentialId = credentialDAO.getCredentialIdByPhone(doctor.getPhone());
        if (doctor.getId() != doctorDAO.getDoctorIdByCredentialId(credentialId)) {
            return credentialDAO.isPhoneUnique(doctor.getPhone());
        }
        return true;
    }

    public boolean isSpecializationIdValid(DoctorDTO doctor) {
        return specializationDAO.getSpecializationById(doctor.getId()) != null;
    }

    public boolean isSpecializationIdEmpty(DoctorDTO doctor) {
        return doctor.getSpecializationId() == null;
    }

    public boolean isIdEmpty(final Integer doctorId) {
        return doctorId == null;
    }

    public boolean idExists(final Integer doctorId) {
        return doctorDAO.getDoctorById(doctorId) != null;
    }

    public boolean idExists(final DoctorDTO doctorDTO) {
        return doctorDAO.getDoctorById(doctorDTO.getId()) != null;
    }

    public boolean isPhotoValid(DoctorDTO doctor) {
        return doctor.getPhoto() != null && doctor.getPhoto().length > 0;
    }
}
