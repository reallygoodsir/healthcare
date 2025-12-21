package com.really.good.sir.service;

import com.really.good.sir.converter.UserSessionConverter;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.UserSessionDTO;
import com.really.good.sir.entity.UserSessionEntity;

public class UserSessionService {
    private final UserSessionDAO userSessionDAO = new UserSessionDAO();
    private final UserSessionConverter converter = new UserSessionConverter();

    public UserSessionDTO getSessionById(int sessionId) {
        UserSessionEntity session = userSessionDAO.getSessionById(sessionId);
        return converter.convert(session);
    }

    public boolean deleteSessionById(int sessionId) {
        return userSessionDAO.deleteSessionById(sessionId);
    }

    public UserSessionDTO authorize(String email, String password) {
        UserSessionEntity sessionEntity = userSessionDAO.authorize(email, password);
        return converter.convert(sessionEntity);
    }
}
