package com.really.good.sir.service;

import com.really.good.sir.converter.PatientConverter;
import com.really.good.sir.dao.PatientDAO;
import com.really.good.sir.dto.PatientDTO;
import com.really.good.sir.entity.PatientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientDAO patientDAO;
    private final PatientConverter patientConverter;

    @Autowired
    public PatientService(PatientDAO patientDAO, PatientConverter patientConverter) {
        this.patientDAO = patientDAO;
        this.patientConverter = patientConverter;
    }

    public List<PatientDTO> getAllPatients() {
        final List<PatientEntity> patientEntities = patientDAO.getAllPatients();
        return patientConverter.convert(patientEntities);
    }

    public int getPatientIdByCredentialId(Integer credentialId) {
        return patientDAO.getPatientIdByCredentialId(credentialId);
    }

    public PatientDTO getPatientById(Integer patientId) {
        PatientEntity patient = patientDAO.getPatientById(patientId);
        return patientConverter.convert(patient);
    }

    public PatientDTO getPatientByPhone(String phoneNumber) {
        PatientEntity patient = patientDAO.getPatientByPhone(phoneNumber);
        return patientConverter.convert(patient);
    }

    public PatientDTO createPatient(PatientDTO patientDTO) {
        final PatientEntity patientEntity = patientConverter.convert(patientDTO);
        final PatientEntity createdEntity = patientDAO.createPatient(patientEntity);
        return patientConverter.convert(createdEntity);
    }

    public PatientDTO updatePatient(PatientDTO patientDTO) {
        final PatientEntity patientEntity = patientConverter.convert(patientDTO);
        final boolean updated = patientDAO.updatePatient(patientEntity);
        if (updated) {
            return patientConverter.convert(patientEntity);
        } else {
            return null;
        }
    }

    public boolean deletePatient(final Integer patientId) {
        return patientDAO.deletePatient(patientId);
    }
}