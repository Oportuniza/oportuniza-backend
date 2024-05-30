package org.oportuniza.oportunizabackend.applications.model;

import jakarta.persistence.*;
import lombok.Data;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.users.model.Applicant;

@Data
@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @ManyToOne
    @JoinColumn(name = "applicant_id")
    private Applicant applicant;
}
