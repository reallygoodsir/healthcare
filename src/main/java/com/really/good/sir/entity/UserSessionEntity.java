package com.really.good.sir.entity;

import java.sql.Timestamp;

public class UserSessionEntity {
    private int id;
    private int credentialId;
    private Timestamp loginDateTime;
    private String role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(int credentialId) {
        this.credentialId = credentialId;
    }

    public Timestamp getLoginDateTime() {
        return loginDateTime;
    }

    public void setLoginDateTime(Timestamp loginDateTime) {
        this.loginDateTime = loginDateTime;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserSessionEntity{" +
                "id=" + id +
                ", credentialId=" + credentialId +
                ", loginDateTime=" + loginDateTime +
                ", role='" + role + '\'' +
                '}';
    }
}
