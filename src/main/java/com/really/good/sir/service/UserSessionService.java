package com.really.good.sir.service;

import com.really.good.sir.converter.UserSessionConverter;
import com.really.good.sir.dao.UserSessionDAO;
import com.really.good.sir.dto.UserSessionDTO;
import com.really.good.sir.entity.UserSessionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSessionService {

    private final UserSessionDAO userSessionDAO;
    private final UserSessionConverter converter;

    @Autowired
    public UserSessionService(UserSessionDAO userSessionDAO, UserSessionConverter converter) {
        this.userSessionDAO = userSessionDAO;
        this.converter = converter;
    }

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