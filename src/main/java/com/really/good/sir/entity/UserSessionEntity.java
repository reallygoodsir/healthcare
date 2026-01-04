package com.really.good.sir.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user_sessions")
@NamedQuery(name = "UserSessionEntity.getSessionById", query = "SELECT us FROM UserSessionEntity us WHERE us.id = :sessionId")
@NamedQuery(name = "UserSessionEntity.deleteById", query = "DELETE FROM UserSessionEntity us WHERE us.id = :sessionId")
public class UserSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "credential_id")
    private int credentialId;

    @Basic
    @Column(name = "login_date_time")
    private Timestamp loginDateTime;

    @Transient
    private String role;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCredentialId() { return credentialId; }
    public void setCredentialId(int credentialId) { this.credentialId = credentialId; }
    public Timestamp getLoginDateTime() { return loginDateTime; }
    public void setLoginDateTime(Timestamp loginDateTime) { this.loginDateTime = loginDateTime; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
