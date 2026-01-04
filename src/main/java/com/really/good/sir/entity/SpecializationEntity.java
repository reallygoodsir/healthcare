package com.really.good.sir.entity;

import javax.persistence.*;

@Entity
@Table(name = "specializations")
@NamedQuery(name = "Specialization.findAll", query = "SELECT s FROM SpecializationEntity s")
public class SpecializationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "name", nullable = false)
    private String name;

    public SpecializationEntity() {}
    public SpecializationEntity(int id, String name) { this.id = id; this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
