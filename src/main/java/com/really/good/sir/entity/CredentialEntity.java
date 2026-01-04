package com.really.good.sir.entity;

import javax.persistence.*;

@Entity
@Table(name = "credentials")
@NamedQuery(
        name = "CredentialEntity.getCredentialByEmail",
        query = "SELECT c.credentialId, c.passwordHash, c.role FROM CredentialEntity c WHERE c.email = :email"
)
public class CredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credential_id")
    private Integer credentialId;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    public Integer getCredentialId() { return credentialId; }
    public void setCredentialId(Integer credentialId) { this.credentialId = credentialId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
