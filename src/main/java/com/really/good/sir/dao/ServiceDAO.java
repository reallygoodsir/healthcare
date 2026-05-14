package com.really.good.sir.dao;

import com.really.good.sir.entity.ServiceEntity;
import com.really.good.sir.repository.ServiceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ServiceDAO {

    private static final Logger LOGGER = LogManager.getLogger(ServiceDAO.class);

    private final ServiceRepository serviceRepository;

    public ServiceDAO(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /* =========================
       CREATE
       ========================= */
    @Transactional
    public ServiceEntity createService(ServiceEntity service) {
        try {
            service.setId(null); // required for IDENTITY insert
            return serviceRepository.saveAndFlush(service);
        } catch (Exception e) {
            LOGGER.error("Error creating service", e);
            return null;
        }
    }

    /* =========================
       READ - ALL
       ========================= */
    @Transactional(readOnly = true)
    public List<ServiceEntity> getAllServices() {
        try {
            return serviceRepository.findAll();
        } catch (Exception e) {
            LOGGER.error("Error fetching all services", e);
            return null;
        }
    }

    /* =========================
       READ - BY ID
       ========================= */
    @Transactional(readOnly = true)
    public ServiceEntity getServiceById(Integer id) {
        try {
            return serviceRepository.findById(id).orElse(null);
        } catch (Exception e) {
            LOGGER.error("Error fetching service by id {}", id, e);
            return null;
        }
    }

    /* =========================
       UPDATE
       ========================= */
    @Transactional
    public boolean updateService(ServiceEntity service) {
        try {
            serviceRepository.save(service);
            return true;
        } catch (Exception e) {
            LOGGER.error("Error updating service", e);
            return false;
        }
    }

    /* =========================
       DELETE
       ========================= */
    @Transactional
    public boolean deleteService(Integer id) {
        try {
            if (!serviceRepository.existsById(id)) {
                return false;
            }

            serviceRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            LOGGER.error("Error deleting service", e);
            return false;
        }
    }

    /* =========================
       CHECK SERVICE NAME EXISTS
       ========================= */
    @Transactional(readOnly = true)
    public boolean isServiceNameExists(String name, Integer excludeId) {
        try {
            if (excludeId != null && excludeId > 0) {
                return serviceRepository.existsByNameAndIdNot(name, excludeId);
            }

            return serviceRepository.existsByName(name);
        } catch (Exception e) {
            LOGGER.error("Error checking service name existence", e);
            return false;
        }
    }
}