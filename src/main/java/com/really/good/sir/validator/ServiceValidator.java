package com.really.good.sir.validator;

import com.really.good.sir.dao.ServiceDAO;
import com.really.good.sir.dto.ServiceDTO;

public class ServiceValidator {

    private static final String NAME_REGEX = "^[A-Za-z0-9\\s'-]{2,50}$";
    private final ServiceDAO serviceDAO = new ServiceDAO();

    public boolean isNameValid(ServiceDTO service) {
        String name = service.getName();
        return name != null && name.matches(NAME_REGEX);
    }

    public boolean isNameUnique(ServiceDTO service) {
        int id = service.getId();
        return !serviceDAO.isServiceNameExists(service.getName(), id);
    }

    public boolean isPriceValid(ServiceDTO service) {
        return service.getPrice() > 0;
    }
}
