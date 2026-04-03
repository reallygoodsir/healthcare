package com.really.good.sir.entity;

public enum Role {
    ADMIN,
    DOCTOR,
    PATIENT,
    CALL_CENTER_AGENT;

    public String asAuthority() {
        return "ROLE_" + name();    }
}