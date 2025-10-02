package com.really.good.sir.entity;

import java.sql.Date;
import java.sql.Time;

public class PatientAppointmentEntity {
    private int appointmentId;
    private int patientId;
    private int serviceId;
    private int doctorId; // <-- Added this
    private Date date;
    private Time startTime;
    private Time endTime;
    private String status;

    // Extra fields for frontend display
    private String patientFirstName;
    private String patientLastName;
    private String serviceName;

    // Getters and setters
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public int getDoctorId() { return doctorId; } // <-- Added getter
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; } // <-- Added setter

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }

    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPatientFirstName() { return patientFirstName; }
    public void setPatientFirstName(String patientFirstName) { this.patientFirstName = patientFirstName; }

    public String getPatientLastName() { return patientLastName; }
    public void setPatientLastName(String patientLastName) { this.patientLastName = patientLastName; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
}
