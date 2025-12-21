package com.really.good.sir.service;

import com.really.good.sir.converter.DoctorScheduleConverter;
import com.really.good.sir.dao.DoctorScheduleDAO;
import com.really.good.sir.dto.DoctorScheduleDTO;
import com.really.good.sir.entity.DoctorScheduleEntity;

import java.util.List;

public class DoctorScheduleService {
    private final DoctorScheduleConverter scheduleConverter = new DoctorScheduleConverter();
    private final DoctorScheduleDAO scheduleDAO = new DoctorScheduleDAO();

    public List<DoctorScheduleDTO> getSchedulesByDoctor(Integer doctorId) {
        List<DoctorScheduleEntity> schedulesByDoctor = scheduleDAO.getSchedulesByDoctor(doctorId);
        return scheduleConverter.convert(schedulesByDoctor);
    }

    public List<DoctorScheduleDTO> getSchedulesForTodayWithAppointments(Integer doctorId) {
        final List<DoctorScheduleEntity> schedules = scheduleDAO.getSchedulesForTodayWithAppointments(doctorId);
        return scheduleConverter.convert(schedules);
    }

    public DoctorScheduleDTO createSchedule(DoctorScheduleDTO requestScheduleDTO) {
        final DoctorScheduleEntity scheduleEntity = scheduleConverter.convert(requestScheduleDTO);
        final DoctorScheduleEntity createdEntity = scheduleDAO.createSchedule(scheduleEntity);
        return scheduleConverter.convert(createdEntity);
    }

    public DoctorScheduleDTO updateSchedule(DoctorScheduleDTO requestScheduleDTO) {
        final DoctorScheduleEntity scheduleEntity = scheduleConverter.convert(requestScheduleDTO);
        boolean updated = scheduleDAO.updateSchedule(scheduleEntity);
        if(updated){
            return scheduleConverter.convert(scheduleEntity);
        }else{
            return null;
        }
    }

    public boolean deleteSchedule(final Integer scheduleId) {
        return scheduleDAO.deleteSchedule(scheduleId);
    }
}
