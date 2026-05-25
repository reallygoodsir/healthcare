package com.really.good.sir.repository;

import com.really.good.sir.entity.AppointmentEntity;
import com.really.good.sir.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Integer> {

    List<AppointmentEntity> findByStatus(AppointmentStatus status);
}