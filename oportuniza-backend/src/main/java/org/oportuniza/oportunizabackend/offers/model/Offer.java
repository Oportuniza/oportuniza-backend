package org.oportuniza.oportunizabackend.offers.model;

import jakarta.persistence.*;
import lombok.Data;
import org.oportuniza.oportunizabackend.applications.model.Application;

import java.util.List;

@Data
@Entity
@Table(name = "offers")
@Inheritance(strategy = InheritanceType.JOINED)
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private boolean negotiable;

    @OneToMany(mappedBy = "offer")
    private List<Application> applications;
}
