package com.really.good.sir.entity;

import javax.persistence.*;

@Entity
@Table(name = "service_appointment_outcomes")
@NamedQuery(name = "PatientAppointmentOutcomeEntity.getByAppointmentId", query = "SELECT p FROM PatientAppointmentOutcomeEntity p WHERE p.appointmentId = :appointmentId")
public class PatientAppointmentOutcomeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "appointment_id", nullable = false, unique = true)
    private Integer appointmentId;

    @Basic
    @Column(name = "result", length = 45)
    private String result;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}
