package org.oportuniza.oportunizabackend.users.model;

import jakarta.persistence.*;
import lombok.Data;
import org.oportuniza.oportunizabackend.applications.model.Application;

import java.util.List;

@Data
@Entity
@Table(name = "applicants")
public class Applicant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "applicant")
    private List<Application> applications;
}
