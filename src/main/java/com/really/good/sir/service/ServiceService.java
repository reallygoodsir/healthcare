package com.really.good.sir.service;

import com.really.good.sir.converter.ServiceConverter;
import com.really.good.sir.dao.ServiceDAO;
import com.really.good.sir.dto.ServiceDTO;
import com.really.good.sir.entity.ServiceEntity;

import java.util.List;

public class ServiceService {
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final ServiceConverter serviceConverter = new ServiceConverter();

    public List<ServiceDTO> getAllServices(){
        List<ServiceEntity> entities = serviceDAO.getAllServices();
        return serviceConverter.convert(entities);
    }

    public ServiceDTO getServiceById(Integer id){
        ServiceEntity entity = serviceDAO.getServiceById(id);
        return serviceConverter.convert(entity);
    }

    public ServiceDTO createService(ServiceDTO serviceDTO){
        ServiceEntity entity = serviceConverter.convert(serviceDTO);
        ServiceEntity created = serviceDAO.createService(entity);
        return serviceConverter.convert(created);
    }

    public ServiceDTO updateService(ServiceDTO serviceDTO) {
        final ServiceEntity entity = serviceConverter.convert(serviceDTO);
        final boolean updated = serviceDAO.updateService(entity);
        if (updated) {
            return serviceConverter.convert(entity);
        } else {
            return null;
        }
    }

    public boolean deleteService(final Integer serviceId) {
        return serviceDAO.deleteService(serviceId);
    }
}
