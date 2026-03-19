package com.really.good.sir.service;

import com.really.good.sir.converter.DoctorConverter;
import com.really.good.sir.dao.DoctorDAO;
import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.entity.DoctorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorDAO doctorDAO;
    private final DoctorConverter doctorConverter;

    @Autowired
    public DoctorService(DoctorDAO doctorDAO, DoctorConverter doctorConverter) {
        this.doctorDAO = doctorDAO;
        this.doctorConverter = doctorConverter;
    }

    public List<DoctorDTO> getAllDoctors() {
        List<DoctorEntity> doctorEntities = doctorDAO.getAllDoctors();
        return doctorConverter.convert(doctorEntities);
    }

    public DoctorDTO getDoctorById(Integer doctorId) {
        DoctorEntity doctorEntity = doctorDAO.getDoctorById(doctorId);
        return doctorConverter.convert(doctorEntity);
    }

    public List<DoctorDTO> getDoctorsByServiceId(Integer serviceId) {
        List<DoctorEntity> doctorEntities = doctorDAO.getDoctorsByServiceId(serviceId);
        return doctorConverter.convert(doctorEntities);
    }

    public int getDoctorIdByCredentialId(Integer credentialId) {
        return doctorDAO.getDoctorIdByCredentialId(credentialId);
    }

    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        DoctorEntity doctorEntity = doctorConverter.convert(doctorDTO);
        DoctorEntity createdDoctorEntity = doctorDAO.createDoctor(doctorEntity);
        return doctorConverter.convert(createdDoctorEntity);
    }

    public DoctorDTO updateDoctor(DoctorDTO doctorDTO) {
        DoctorEntity doctorEntity = doctorConverter.convert(doctorDTO);
        DoctorEntity updatedDoctorEntity = doctorDAO.updateDoctor(doctorEntity);
        return updatedDoctorEntity != null ? doctorConverter.convert(updatedDoctorEntity) : null;
    }

    public boolean deleteDoctor(Integer doctorId) {
        return doctorDAO.deleteDoctor(doctorId);
    }
}