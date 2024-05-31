package org.oportuniza.oportunizabackend.offers.model;

import jakarta.persistence.*;
import lombok.Data;
import org.oportuniza.oportunizabackend.applications.model.Application;

import java.util.List;

@Data
@Entity
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "offer")
    private List<Application> applications;
}
