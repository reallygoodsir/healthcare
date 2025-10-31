package com.really.good.sir.validator;

import com.really.good.sir.dao.CredentialDAO;
import com.really.good.sir.dao.PatientDAO;
import com.really.good.sir.dto.PatientDTO;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PatientValidator {

    private static final String NAME_REGEX = "^[A-Za-z\\s'-]{2,25}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^[+()\\d\\s-]{7,20}$";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final CredentialDAO credentialDAO = new CredentialDAO();
    private final PatientDAO patientDAO = new PatientDAO();

    public boolean isFirstNameValid(PatientDTO patient) {
        String firstName = patient.getFirstName();
        return firstName != null && firstName.matches(NAME_REGEX);
    }

    public boolean isLastNameValid(PatientDTO patient) {
        String lastName = patient.getLastName();
        return lastName != null && lastName.matches(NAME_REGEX);
    }

    public boolean isEmailValid(PatientDTO patient) {
        String email = patient.getEmail();
        if (email == null || !email.matches(EMAIL_REGEX)) {
            return false;
        }
        int credentialId = credentialDAO.getCredentialIdByEmail(email);
        if (patient.getId() != patientDAO.getPatientIdByCredentialId(credentialId)) {
            return credentialDAO.isEmailUnique(email);
        }
        return true;
    }

    public boolean isPhoneValid(PatientDTO patient) {
        String phone = patient.getPhone();
        if (phone == null || !phone.matches(PHONE_REGEX)) {
            return false;
        }
        int credentialId = credentialDAO.getCredentialIdByPhone(phone);
        if (patient.getId() != patientDAO.getPatientIdByCredentialId(credentialId)) {
            return credentialDAO.isPhoneUnique(phone);
        }
        return true;
    }

    public boolean isDateOfBirthValid(PatientDTO patient) {
        String dateOfBirth = patient.getDateOfBirth();
        if (dateOfBirth == null || dateOfBirth.isEmpty()) {
            return false;
        }
        try {
            LocalDate dob = LocalDate.parse(dateOfBirth, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            return !dob.isAfter(today) && Period.between(dob, today).getYears() >= 13;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public boolean isAddressValid(PatientDTO patient) {
        String address = patient.getAddress();
        return address != null && !address.trim().isEmpty();
    }
}
