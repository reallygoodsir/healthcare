package com.really.good.sir.service;

import com.really.good.sir.converter.DoctorScheduleConverter;
import com.really.good.sir.dao.DoctorScheduleDAO;
import com.really.good.sir.dto.DoctorScheduleDTO;
import com.really.good.sir.entity.DoctorScheduleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorScheduleService {

    private final DoctorScheduleDAO scheduleDAO;
    private final DoctorScheduleConverter scheduleConverter;

    @Autowired
    public DoctorScheduleService(DoctorScheduleDAO scheduleDAO, DoctorScheduleConverter scheduleConverter) {
        this.scheduleDAO = scheduleDAO;
        this.scheduleConverter = scheduleConverter;
    }

    public List<DoctorScheduleDTO> getSchedulesByDoctor(Integer doctorId) {
        List<DoctorScheduleEntity> schedulesByDoctor = scheduleDAO.getSchedulesByDoctor(doctorId);
        return scheduleConverter.convert(schedulesByDoctor);
    }

    public List<DoctorScheduleDTO> getSchedulesForTodayWithAppointments(Integer doctorId) {
        List<DoctorScheduleEntity> schedules = scheduleDAO.getSchedulesForTodayWithAppointments(doctorId);
        return scheduleConverter.convert(schedules);
    }

    public DoctorScheduleDTO createSchedule(DoctorScheduleDTO requestScheduleDTO) {
        DoctorScheduleEntity scheduleEntity = scheduleConverter.convert(requestScheduleDTO);
        DoctorScheduleEntity createdEntity = scheduleDAO.createSchedule(scheduleEntity);
        return scheduleConverter.convert(createdEntity);
    }

    public DoctorScheduleDTO updateSchedule(DoctorScheduleDTO requestScheduleDTO) {
        DoctorScheduleEntity scheduleEntity = scheduleConverter.convert(requestScheduleDTO);
        boolean updated = scheduleDAO.updateSchedule(scheduleEntity);
        return updated ? scheduleConverter.convert(scheduleEntity) : null;
    }

    public boolean deleteSchedule(Integer scheduleId) {
        return scheduleDAO.deleteSchedule(scheduleId);
    }
}