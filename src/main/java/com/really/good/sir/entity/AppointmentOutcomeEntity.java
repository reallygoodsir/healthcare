package com.really.good.sir.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "appointment_outcomes")
@NamedQuery(name = "AppointmentOutcome.findByAppointmentId", query = "SELECT a FROM AppointmentOutcomeEntity a WHERE a.appointmentId = :appointmentId")
public class AppointmentOutcomeEntity {

    @Id
    @Column(name = "appointment_id")
    private Integer appointmentId;

    @Basic
    @Column(name = "diagnosis")
    private String diagnosis;

    @Basic
    @Column(name = "recommendations")
    private String recommendations;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
