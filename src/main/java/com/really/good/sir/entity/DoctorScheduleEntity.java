package com.really.good.sir.entity;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "doctor_schedules")
public class DoctorScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "doctor_id", nullable = false)
    private Integer doctorId;

    @Basic
    @Column(name = "schedule_date", nullable = false)
    private Date scheduleDate;

    @Basic
    @Column(name = "start_time", nullable = false)
    private Time startTime;

    @Basic
    @Column(name = "end_time", nullable = false)
    private Time endTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }
    public Date getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(Date scheduleDate) { this.scheduleDate = scheduleDate; }
    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
}
