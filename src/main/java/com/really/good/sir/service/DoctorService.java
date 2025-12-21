package com.really.good.sir.service;

import com.really.good.sir.converter.DoctorConverter;
import com.really.good.sir.dao.DoctorDAO;
import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.entity.DoctorEntity;

import java.util.List;

public class DoctorService {
    private final DoctorConverter doctorConverter = new DoctorConverter();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    public List<DoctorDTO> getAllDoctors() {
        final List<DoctorEntity> doctorEntities = doctorDAO.getAllDoctors();
        return doctorConverter.convert(doctorEntities);
    }

    public DoctorDTO getDoctorById(final Integer doctorId) {
        final DoctorEntity doctorEntity = doctorDAO.getDoctorById(doctorId);
        return doctorConverter.convert(doctorEntity);
    }

    public List<DoctorDTO> getDoctorsByServiceId(final Integer serviceId) {
        final List<DoctorEntity> doctorEntities = doctorDAO.getDoctorsByServiceId(serviceId);
        return doctorConverter.convert(doctorEntities);
    }

    public int getDoctorIdByCredentialId(final Integer credentialId) {
        return doctorDAO.getDoctorIdByCredentialId(credentialId);
    }

    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        final DoctorEntity doctorEntity = doctorConverter.convert(doctorDTO);
        final DoctorEntity createdDoctorEntity = doctorDAO.createDoctor(doctorEntity);
        return doctorConverter.convert(createdDoctorEntity);
    }

    public DoctorDTO updateDoctor(DoctorDTO doctorDTO) {
        final DoctorEntity doctorEntity = doctorConverter.convert(doctorDTO);
        final boolean isDoctorUpdated = doctorDAO.updateDoctor(doctorEntity);
        if (isDoctorUpdated) {
            return doctorConverter.convert(doctorEntity);
        } else {
            return null;
        }
    }

    public boolean deleteDoctor(final Integer doctorId) {
        return doctorDAO.deleteDoctor(doctorId);
    }
}
