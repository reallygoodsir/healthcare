package com.really.good.sir.scheduler;

import com.really.good.sir.dao.AppointmentDAO;
import com.really.good.sir.dao.DoctorScheduleDAO;
import com.really.good.sir.entity.AppointmentEntity;
import com.really.good.sir.entity.DoctorScheduleEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AppointmentCleanupScheduler {

    private final AppointmentDAO appointmentDAO;
    private final DoctorScheduleDAO scheduleDAO;

    public AppointmentCleanupScheduler(
            AppointmentDAO appointmentDAO,
            DoctorScheduleDAO scheduleDAO
    ) {
        this.appointmentDAO = appointmentDAO;
        this.scheduleDAO = scheduleDAO;
    }

    @Scheduled(fixedRate = 60000)
    public void cancelExpiredAppointments() {

        LocalDateTime now = LocalDateTime.now();
        System.out.println("\n\t-----");
        List<AppointmentEntity> appointments =
                appointmentDAO.findScheduledAppointments();

        for (AppointmentEntity appointment : appointments) {

            DoctorScheduleEntity schedule =
                    scheduleDAO.getById(appointment.getScheduleId());

            if (schedule == null) continue;

            LocalDateTime end =
                    LocalDateTime.of(
                            schedule.getScheduleDate().toLocalDate(),
                            schedule.getEndTime().toLocalTime()
                    );

            if (end.isBefore(now)) {

                appointmentDAO.updateAppointmentStatus(
                        appointment.getAppointmentId(),
                        "CANCELLED"
                );
            }
        }
        System.out.println("\n\t-----");
    }
}