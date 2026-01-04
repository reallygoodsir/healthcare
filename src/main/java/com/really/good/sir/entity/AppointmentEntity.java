package com.really.good.sir.entity;

import javax.persistence.*;

@Entity
@Table(name = "appointments")
@NamedQuery(
        name = "Appointment.updateStatus",
        query = "UPDATE AppointmentEntity a SET a.status = :status WHERE a.appointmentId = :id"
)
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Integer appointmentId;

    @Basic
    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    @Basic
    @Column(name = "doctor_id", nullable = false)
    private Integer doctorId;

    @Basic
    @Column(name = "schedule_id", nullable = false)
    private Integer scheduleId;

    @Basic
    @Column(name = "status", nullable = false)
    private String status;

    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }
    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }
    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }
    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
